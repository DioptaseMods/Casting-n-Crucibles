package org.dioptase.castingncrucibles.content.casting.refractory_basin;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.dioptase.castingncrucibles.registry.CNCBlockEntities;
import org.dioptase.castingncrucibles.registry.CNCLang;
import org.dioptase.castingncrucibles.registry.CNCShapes;

public class RefractoryBasinBlock extends Block implements IWrenchable, IBE<RefractoryBasinBlockEntity> {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public RefractoryBasinBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, Shape.PLAIN));
    }

    public static boolean isRefractoryBasin(BlockState state) {
        return state.getBlock() instanceof RefractoryBasinBlock;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (movedByPiston)
            return;
        withBlockEntityDo(level, pos, RefractoryBasinBlockEntity::updateConnectivity);

        BlockState newState = level.getBlockState(pos);
        if (state != newState && newState.getBlock() == this) {
            level.markAndNotifyBlock(pos, level.getChunkAt(pos), oldState, newState, UPDATE_ALL_IMMEDIATE, 512);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof RefractoryBasinBlockEntity RefBasinBE))
                return;
            ItemHelper.dropContents(level, pos, RefBasinBE.inventory);
            level.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(RefBasinBE);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(TOP, BOTTOM, SHAPE));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Boolean bottom = state.getValue(BOTTOM);
        Shape shape = state.getValue(SHAPE);
        Direction direction = shape.toDirection();

        if (bottom)
            return switch (shape) {
            case NE, SE, NW, SW -> CNCShapes.REF_BASIN_CORNER_BOTTOM.get(direction);
            case NORTH, SOUTH, EAST, WEST -> CNCShapes.REF_BASIN_WALL_BOTTOM.get(direction);
            case PLAIN -> CNCShapes.REF_BASIN_SINGLE_BOTTOM;
            default -> CNCShapes.REF_BASIN_BOTTOM;
            };
        else
            return switch (shape) {
            case NE, SE, NW, SW -> CNCShapes.REF_BASIN_CORNER.get(direction);
            case NORTH, SOUTH, EAST, WEST -> CNCShapes.REF_BASIN_WALL.get(direction);
            case PLAIN -> CNCShapes.REF_BASIN_SINGLE;
            default -> Shapes.empty();
            };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean onClient = level.isClientSide;

        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.isCreative())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        FluidHelper.FluidExchange exchange = null;
        RefractoryBasinBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), level, pos);
        if (be == null)
            return ItemInteractionResult.FAIL;

        IFluidHandler tankCapability = level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (tankCapability == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        FluidStack prevFluidInTank = tankCapability.getFluidInTank(0)
                .copy();

        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack))
                return ItemInteractionResult.SUCCESS;
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInTank = tankCapability.getFluidInTank(0);

        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            Fluid fluid = fluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInTank);
        }

        if (exchange == FluidHelper.FluidExchange.TANK_TO_ITEM) {
            Fluid fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInTank);
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth
                    .clamp(1 - (1f * fluidInTank.getAmount() / (FluidTankBlockEntity.getCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.random.nextFloat() - .5f) / 4f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!FluidStack.isSameFluidSameComponents(fluidInTank, prevFluidInTank)) {
            if (be instanceof RefractoryBasinBlockEntity) {
                RefractoryBasinBlockEntity controllerBE = be.getControllerBE();
                if (controllerBE != null) {
                    if (fluidState != null && onClient) {
                        BlockParticleOption blockParticleData =
                                new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                        float fluidLevel = (float) fluidInTank.getAmount() / tankCapability.getTankCapacity(0);

                        boolean reversed = fluidInTank.getFluid()
                                .getFluidType()
                                .isLighterThanAir();
                        if (reversed)
                            fluidLevel = 1 - fluidLevel;

                        Vec3 vec = hitResult.getLocation();
                        vec = new Vec3(vec.x, controllerBE.getBlockPos()
                                .getY() + fluidLevel * (controllerBE.height - .5f) + .25f, vec.z);
                        Vec3 motion = player.position()
                                .subtract(vec)
                                .scale(1 / 20f);
                        vec = vec.add(motion);
                        level.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                        return ItemInteractionResult.SUCCESS;
                    }
                    controllerBE.sendDataImmediately();
                    controllerBE.setChanged();
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public Class<RefractoryBasinBlockEntity> getBlockEntityClass() {
        return RefractoryBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefractoryBasinBlockEntity> getBlockEntityType() {
        return CNCBlockEntities.REFRACTORY_BASIN_BE.get();
    }

//    @Override
//    public boolean hasAnalogOutputSignal(BlockState state) {
//        return true;
//    }

//    @Override
//    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
//        return super.getAnalogOutputSignal(state, level, pos);
//    }

    public enum Shape implements StringRepresentable {
        PLAIN,
        INNER,
        NW, SW, NE, SE,
        NORTH, SOUTH, WEST, EAST;

        @Override
        public String getSerializedName() {
            return CNCLang.asId(name());
        }

        public boolean isWall() {
            return this.equals(NORTH) || this.equals(SOUTH) || this.equals(EAST) || this.equals(WEST);
        }

        public boolean isCorner() {
            return this.equals(NW) || this.equals(SW) || this.equals(NE) ||  this.equals(SE);
        }

        public Direction toDirection() {
            return switch(this) {
                case NE, NORTH -> Direction.NORTH;
                case NW, WEST -> Direction.WEST;
                case SW, SOUTH -> Direction.SOUTH;
                case SE, EAST -> Direction.EAST;
                default -> Direction.DOWN;
            };
        }
    }
}
