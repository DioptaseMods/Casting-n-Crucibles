package org.dioptase.castingncrucibles;

import com.tterrag.registrate.providers.ProviderType;
import org.dioptase.castingncrucibles.registry.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CastingNCrucibles.MOD_ID)
public class CastingNCrucibles {
    public static final String MOD_ID = "casting_n_crucibles";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CNCRegistrate REGISTRATE = new CNCRegistrate(MOD_ID);
    public CastingNCrucibles(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        REGISTRATE.registerEventListeners(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        CNCCreativeTabs.register(modEventBus);
        CNCDataComponents.register(modEventBus);
        modEventBus.addListener(CNCCreativeTabs::addCreative);

        CNCPartialModels.init();
        CNCBlocks.init();
        CNCBlockEntities.init();
        CNCItems.init();
        CNCTags.init();

        REGISTRATE.addDataGenerator(ProviderType.LANG, prov -> {
            // UI & Tooltips
            prov.add("creativetab.casting_n_crucibles.main_tab", "Casting 'N' Crucibles");
        });


        // modContainer.registerConfig(ModConfig.Type.COMMON, CNCConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
