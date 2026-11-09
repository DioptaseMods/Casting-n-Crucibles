package org.dioptase.castingncrucibles.registry;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;

import static org.dioptase.castingncrucibles.registry.CNCPartialModels.getCT;

public class CNCSpriteShifts {
    public static final CTSpriteShiftEntry REFRACTORY_BASIN = rectangle("refractory_basin"),
    REFRACTORY_BASIN_INNER = rectangle("refractory_basin_inner"),
    REFRACTORY_BASIN_BOTTOM = rectangle("refractory_basin_bottom");

    private static CTSpriteShiftEntry rectangle(String name) {
        return getCT(AllCTTypes.RECTANGLE, name);
    }
}
