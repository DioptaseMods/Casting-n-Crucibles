package org.dioptase.castingncrucibles.foundation.data.recipe;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public enum OreTier {
    POOR("poor_", BlockTags.NEEDS_STONE_TOOL, ConstantValue.exactly(1.0F)),
    NORMAL("", BlockTags.NEEDS_STONE_TOOL, UniformGenerator.between(1.0F, 2.0F)),
    RICH("rich_", BlockTags.NEEDS_STONE_TOOL, UniformGenerator.between(2.0F, 4.0F));

    private final String prefix;
    private final TagKey<Block> harvestTag;
    private final NumberProvider countProvider;

    OreTier(String prefix, TagKey<Block> harvestTag, NumberProvider countProvider) {
        this.prefix = prefix;
        this.harvestTag = harvestTag;
        this.countProvider = countProvider;
    }

    public String getPrefix() { return prefix; }
    public TagKey<Block> getHarvestTag() { return harvestTag; }
    public NumberProvider getCountProvider() { return countProvider; }
}