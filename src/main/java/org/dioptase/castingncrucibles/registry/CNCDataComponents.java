package org.dioptase.castingncrucibles.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dioptase.castingncrucibles.CastingNCrucibles;
import org.dioptase.castingncrucibles.foundation.data.MetalComposition;

public class CNCDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CastingNCrucibles.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MetalComposition>> METAL_COMPOSITION =
            DATA_COMPONENTS.registerComponentType(
                    "metal_composition",
                    builder -> builder
                            .persistent(MetalComposition.CODEC)
                            .networkSynchronized(MetalComposition.STREAM_CODEC)
            );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
