package org.dioptase.castingncrucibles.content.casting.refractory_basin.basin;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class RefractoryBasinTank implements IFluidHandler {
    public int capacity;

    protected Runnable updateCallback;
    public RefractoryBasinTank(int capacity, Runnable updateCallback) {
        this.capacity = capacity;
        this.updateCallback = updateCallback;
    }

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public FluidStack getFluidInTank(int i) {
        return null;
    }

    @Override
    public int getTankCapacity(int i) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int i, FluidStack fluidStack) {
        return false;
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        return null;
    }

    @Override
    public FluidStack drain(int i, FluidAction fluidAction) {
        return null;
    }
}
