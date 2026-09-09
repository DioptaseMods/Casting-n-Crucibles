package org.dioptase.castingncrucibles.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CNCTags {
    public static final TagKey<Item> STEEL = itemTag("steel");
    public static final TagKey<Item> STEEL_INGOT = itemTag("ingots/steel");

    public static final TagKey<Item> PIG_IRON = itemTag("pig_iron");
    public static final TagKey<Item> PIG_IRON_INGOT = itemTag("ingots/pig_iron");

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    public static void init() {

    }
}
