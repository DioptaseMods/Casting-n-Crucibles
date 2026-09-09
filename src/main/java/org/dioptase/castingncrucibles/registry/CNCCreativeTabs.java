package org.dioptase.castingncrucibles.registry;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dioptase.castingncrucibles.CastingNCrucibles;

import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, CastingNCrucibles.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TAB.register("main_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> CNCItems.STEEL_INGOT.asItem().getDefaultInstance())
                    .title(Component.translatable("creativetab.casting_n_crucibles.main_tab"))
                    .build());
//    Need to set up the addCreative method for this
//    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ORES_TAB = CREATIVE_MODE_TAB.register("ores_tab",
//            () -> CreativeModeTab.builder()
//                    .icon(() -> CNCItems.STEEL_INGOT.asItem().getDefaultInstance())
//                    .title(Component.translatable("creativetab.casting_n_crucibles.ores_tab"))
//                    .build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MAIN_TAB.get()) {
            for (RegistryEntry<Item, ?> item : REGISTRATE.getAll(Registries.ITEM)) {
                event.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
            }
        }
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
