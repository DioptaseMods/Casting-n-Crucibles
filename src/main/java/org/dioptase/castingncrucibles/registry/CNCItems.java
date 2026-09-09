package org.dioptase.castingncrucibles.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCItems {
    public static ItemEntry<Item> CARBON_BRICK = REGISTRATE.item("carbon_brick", Item::new)
            .register();
    public static ItemEntry<Item> FIRE_BRICK = REGISTRATE.item("fire_brick", Item::new)
            .register();
    public static ItemEntry<Item> HEMATITE_CHUNK = REGISTRATE.item("hematite_chunk", Item::new)
            .register();
    public static ItemEntry<Item> MAGNETITE_CHUNK = REGISTRATE.item("magnetite_chunk", Item::new)
            .register();
    public static ItemEntry<Item> CASSITERITE_CHUNK = REGISTRATE.item("cassiterite_chunk", Item::new)
            .register();

    public static ItemEntry<Item> STEEL_INGOT = taggedIngredient("steel_ingot", CNCTags.STEEL, CNCTags.STEEL_INGOT);
    public static ItemEntry<Item> PIG_IRON_INGOT = taggedIngredient("pig_iron_ingot", CNCTags.PIG_IRON, CNCTags.PIG_IRON_INGOT);

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    public static void init() {

    }
}
