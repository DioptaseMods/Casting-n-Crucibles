package org.dioptase.castingncrucibles.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.dioptase.castingncrucibles.content.casting.refractory_basin.RefractoryBasinBlockEntity;
import org.dioptase.castingncrucibles.content.casting.refractory_basin.RefractoryBasinRenderer;

import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCBlockEntities {
    public static final BlockEntityEntry<RefractoryBasinBlockEntity> REFRACTORY_BASIN_BE = REGISTRATE
            .blockEntity("refractory_basin", RefractoryBasinBlockEntity::new)
            .validBlock(CNCBlocks.REFRACTORY_BASIN)
            .renderer(() -> RefractoryBasinRenderer::new)
            .register();


    public static void init() {

    }
}
