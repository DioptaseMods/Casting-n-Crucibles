package org.dioptase.castingncrucibles.content.casting.basin;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.dioptase.castingncrucibles.registry.CNCBlockEntities;

import java.util.List;
import java.util.Objects;

import static com.simibubi.create.content.fluids.tank.FluidTankBlockEntity.getCapacityMultiplier;

public class RefractoryBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid, IMultiBlockEntityContainer.Inventory {
    private static final int MAX_SIZE = 4;
    private static final int DEFAULT_CAPACITY_MULTIPLIER = 8000;
    private static final int SLOTS_PER_BLOCK = 3;

    protected IFluidHandler fluidCapability;
    protected ICapabilityProvider<IItemHandler> itemCapability;
    protected SmartFluidTank tankInventory;
    protected ItemStackHandler inventory;

    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean updateCapability;
    protected boolean forceFluidLevelUpdate;

    protected int width;
    protected int height;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // For rendering purposes only
    private LerpedFloat fluidLevel;

    public RefractoryBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = createInventory();
        inventory = new ItemStackHandler(SLOTS_PER_BLOCK) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        updateCapability = false;
        height = 1;
        width = 1;
        refreshCapabilities();
    }

    @Override
    public void tick() {
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (updateConnectivity)
            updateConnectivity();
        if (fluidLevel != null)
            fluidLevel.tickChaser();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CNCBlockEntities.REFRACTORY_BASIN_BE.get(),
                (be, side) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability();
                    return be.fluidCapability;
                }
        );
//        event.registerBlockEntity(
//                Capabilities.ItemHandler.BLOCK,
//                CNCBlockEntities.REFRACTORY_BASIN_BE.get(),
//                (be, side) -> {
//                    if (be.itemCapability == null)
//                        be.refreshCapability();
//                    return be.itemCapability;
//                }
//        )
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    private void refreshCapability() {
        fluidCapability = handlerForFluidCapability();
        invalidateCapabilities();
    }

    private void refreshCapabilities() {
        fluidCapability = handlerForFluidCapability();
        itemCapability = this::getInventoryOfBlock;
        invalidateCapabilities();
    }

    public SmartFluidTank getTankInventory() {
        return tankInventory;
    }

    public ItemStackHandler getInventoryOfBlock() {
        return inventory;
    }

    private IFluidHandler handlerForFluidCapability() {
        return isController() ? tankInventory : ((getControllerBE() != null) ? getControllerBE().handlerForFluidCapability() : new FluidTank(0));
    }

    private SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    RefractoryBasinBlockEntity basinAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (basinAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, basinAt.getBlockState().getBlock());
                }
            }
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear().startWithValue(getFillState());
            fluidLevel.chase(getFillState(), .5f, LerpedFloat.Chaser.EXP);
        }
    }

    public float getFillState() {
        return (float) tankInventory.getFluidAmount() / tankInventory.getCapacity();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public RefractoryBasinBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof RefractoryBasinBlockEntity)
            return (RefractoryBasinBlockEntity) blockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos pos) {
        if ((!hasLevel() || level.isClientSide) && !isVirtual())
            return;
        if (pos.equals(this.controller))
            return;
        this.controller = pos;
        refreshCapabilities();
        setChanged();
        sendData();
    }

    @Override
    public void removeController(boolean keepContents) {
        if (!hasLevel() || level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepContents)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        onFluidStackChanged(tankInventory.getFluid());

        BlockState state = getBlockState();
        if (RefractoryBasinBlock.isRefractoryBasin(state)) {
            state = state.setValue(RefractoryBasinBlock.BOTTOM, true);
            state = state.setValue(RefractoryBasinBlock.TOP, true);
            state = state.setValue(RefractoryBasinBlock.SHAPE, RefractoryBasinBlock.Shape.PLAIN);
            getLevel().setBlock(worldPosition, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        refreshCapabilities();
        setChanged();
        sendData();
    }

    private void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
        int overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            tankInventory.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (RefractoryBasinBlock.isRefractoryBasin(state)) {
            RefractoryBasinBlock.Shape newShape = getShape();

            state = state.setValue(RefractoryBasinBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(RefractoryBasinBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            state = state.setValue(RefractoryBasinBlock.SHAPE, newShape);
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        onFluidStackChanged(tankInventory.getFluid());
        refreshCapabilities();
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return 4;
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    private int getTotalTankSize() {
        return width * width * height;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        controller = null;
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());

            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), IFluidHandler.FluidAction.EXECUTE);
        }

        if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear().startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }

        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || fluidLevel == null)
                fluidLevel = LerpedFloat.linear().startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.put("TankContent", tankInventory.writeToNBT(registries, new CompoundTag()));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }

        compound.put("Inventory", inventory.serializeNBT(registries));

        super.write(compound, registries, clientPacket);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        if (isController()) {
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    private RefractoryBasinBlock.Shape getShape() {
        RefractoryBasinBlock.Shape shape = RefractoryBasinBlock.Shape.PLAIN;
        RefractoryBasinBlockEntity controller = getControllerBE();

        if (controller == null)
            return shape;

        int xOffset = getBlockPos().getX() - controller.getBlockPos().getX();
        int zOffset = getBlockPos().getZ() - controller.getBlockPos().getZ();

        if (width != 1) {
            if (xOffset == 0) {
                if (zOffset == 0)
                    return RefractoryBasinBlock.Shape.NW;
                else if (zOffset == (width - 1))
                    return RefractoryBasinBlock.Shape.SW;
                else
                    return RefractoryBasinBlock.Shape.WEST;


            } else if (xOffset == (width - 1)) {
                if (zOffset == 0)
                    return RefractoryBasinBlock.Shape.NE;
                else if (zOffset == (width - 1))
                    return RefractoryBasinBlock.Shape.SE;
                else
                    return RefractoryBasinBlock.Shape.EAST;

            } else if (zOffset == 0)
                return RefractoryBasinBlock.Shape.NORTH;
            else if (zOffset == (width - 1))
                return RefractoryBasinBlock.Shape.SOUTH;
            else
                return RefractoryBasinBlock.Shape.INNER;
        }
        return shape;
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    public void sendDataImmediately() {
        //sendData() Might need to be modified to be more similar to the @FluidTankBlockEntity.java sendData().
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }
}
