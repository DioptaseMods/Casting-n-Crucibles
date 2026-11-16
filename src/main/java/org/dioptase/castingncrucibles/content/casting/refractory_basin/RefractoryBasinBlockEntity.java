package org.dioptase.castingncrucibles.content.casting.refractory_basin;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.packager.InventoryIdentifier;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.dioptase.castingncrucibles.foundation.utility.CNCUtils;
import org.dioptase.castingncrucibles.registry.CNCBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RefractoryBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid, IMultiBlockEntityContainer.Inventory {
    private static final int MAX_SIZE = 4;
    private static final int DEFAULT_CAPACITY_MULTIPLIER = 8000;
    protected static final int SLOTS_PER_BLOCK = 2;

    protected IFluidHandler fluidCapability;
    protected IItemHandlerModifiable itemCapability = null;
    protected ItemStackHandler inventory;
    protected InventoryIdentifier invId;
    protected SmartFluidTank tankInventory;

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
                if (!level.isClientSide)
                    sendData();
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
            refreshCapabilities();
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
                        be.refreshCapabilities();
                    return be.fluidCapability;
                });

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CNCBlockEntities.REFRACTORY_BASIN_BE.get(),
                (be, context) -> be.getItemCapability()
        );
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    private void refreshCapabilities() {
        fluidCapability = getFluidCapability();
        itemCapability = getItemCapability();
        invalidateCapabilities();
    }

    private IFluidHandler getFluidCapability() {
        return isController() ? tankInventory : ((getControllerBE() != null) ? getControllerBE().getFluidCapability() : new FluidTank(0));
    }

    private IItemHandlerModifiable getItemCapability() {
        return isController() ? inventory : getControllerBE() != null ? getControllerBE().getItemCapability() : itemCapability;
    }

    private SmartFluidTank createInventory() {
        return new SmartFluidTank(DEFAULT_CAPACITY_MULTIPLIER, this::onFluidStackChanged);
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
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        RefractoryBasinBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        containedFluidTooltip(tooltip, isPlayerSneaking,
                level.getCapability(Capabilities.FluidHandler.BLOCK, controllerBE.getBlockPos(), null));
        CNCUtils.createItemTooltip(tooltip, controllerBE.getItemCapability());
        return true;
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
        if (!keepContents) {
            applyFluidTankSize(1);
            applyInventorySize(1);
        }
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

    private void centralizeInventories() {
        if (level == null || level.isClientSide || !isController())
            return;

        applyInventorySize(getTotalTankSize());

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for  (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);

                    if (pos.equals(this.worldPosition))
                        continue;

                    RefractoryBasinBlockEntity part = ConnectivityHandler.partAt(getType(), level, pos);
                    if (part == null)
                        continue;

                    for (int slot = 0; slot < part.inventory.getSlots(); slot++) {
                        ItemStack stack = part.inventory.getStackInSlot(slot).copy();
                        if (stack.isEmpty())
                            continue;

                        ItemStack remainder = ItemHandlerHelper.insertItem(this.inventory, stack, false);

                        part.inventory.setStackInSlot(slot, ItemStack.EMPTY);
                        part.setChanged();

                        if (!remainder.isEmpty()) {
                            Containers.dropItemStack(level,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    remainder);
                        }
                    }
                }
            }
        }
        setChanged();
        sendData();
    }

    protected void applyInventorySize(int blocks) {
        int newSize = blocks * SLOTS_PER_BLOCK;
        if (inventory.getSlots() == newSize)
            return;

        List<ItemStack> currentItems = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            if (!stack.isEmpty())
                currentItems.add(stack);
        }

        inventory.setSize(newSize);

        for (int i = 0; i < currentItems.size(); i++) {
            ItemStack stack = currentItems.get(i);
            if (i < newSize)
                inventory.setStackInSlot(i, stack);
            else if (hasLevel() && !level.isClientSide) {
                Containers.dropItemStack(level,
                        worldPosition.getX() + 0.5f,
                        worldPosition.getY() + 0.5f,
                        worldPosition.getZ() + 0.5f,
                        stack);
            }
        }
    }

    private void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * DEFAULT_CAPACITY_MULTIPLIER);
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
        if (isController())
            centralizeInventories();
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

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * DEFAULT_CAPACITY_MULTIPLIER);
            applyInventorySize(getTotalTankSize());

            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), IFluidHandler.FluidAction.EXECUTE);
            inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
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
            if (isController()) {
                tankInventory.setCapacity(DEFAULT_CAPACITY_MULTIPLIER * getTotalTankSize());
                applyInventorySize(getTotalTankSize());
            }
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
            compound.put("Inventory", inventory.serializeNBT(registries));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }

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
        CNCConnectivityHandler.formMulti(this);
        applyInventorySize(getTotalTankSize());
    }

    public void sendDataImmediately() {
        //sendData() Might need to be modified to be more similar to the @FluidTankBlockEntity.java sendData().
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }
}
