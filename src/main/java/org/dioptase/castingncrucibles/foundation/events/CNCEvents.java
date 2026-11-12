package org.dioptase.castingncrucibles.foundation.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.dioptase.castingncrucibles.content.casting.refractory_basin.RefractoryBasinBlockEntity;

@EventBusSubscriber
public class CNCEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        RefractoryBasinBlockEntity.registerCapabilities(event);
    }
}
