package org.dioptase.castingncrucibles.content.casting.refractory_basin.basin;

import net.createmod.catnip.animation.LerpedFloat;
import org.dioptase.castingncrucibles.content.casting.refractory_basin.RefractoryBasinBlockEntity;

public class RefractoryBasinData {
    private final RefractoryBasinBlockEntity be;
    protected RefractoryBasinItemHandler itemHandler;

    private int currentTemp;
    public boolean needsUpdate;

    public LerpedFloat gauge = LerpedFloat.linear();

    public RefractoryBasinData(RefractoryBasinBlockEntity be) {
        this.be = be;
        this.itemHandler = new RefractoryBasinItemHandler();
    }
}
