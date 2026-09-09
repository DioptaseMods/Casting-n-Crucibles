package org.dioptase.castingncrucibles;

import org.dioptase.castingncrucibles.registry.CNCBlocks;
import org.dioptase.castingncrucibles.registry.CNCItems;
import org.dioptase.castingncrucibles.registry.CNCPartialModels;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CastingNCrucibles.MOD_ID)
public class CastingNCrucibles {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "casting_n_crucibles";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CNCRegistrate REGISTRATE = new CNCRegistrate(MOD_ID);
    public CastingNCrucibles(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        REGISTRATE.registerEventListeners(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        CNCPartialModels.init();
        CNCBlocks.init();
        CNCItems.init();


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        // modContainer.registerConfig(ModConfig.Type.COMMON, CNCConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
