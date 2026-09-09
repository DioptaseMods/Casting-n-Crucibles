package org.dioptase.castingncrucibles.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCItems {
    public static ItemEntry<Item> CARBON_BRICK = REGISTRATE.item("carbon_brick", Item::new)
            .register();
    public static ItemEntry<Item> FIRE_BRICK = REGISTRATE.item("fire_brick", Item::new)
            .register();
    public static ItemEntry<Item> STEEL_INGOT = REGISTRATE.item("steel_ingot", Item::new)
            .register();

    public static void init() {

    }
}
