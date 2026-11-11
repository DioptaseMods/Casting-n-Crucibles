package org.dioptase.castingncrucibles.registry;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.neoforged.neoforge.common.Tags;
import org.dioptase.castingncrucibles.content.casting.basin.RefractoryBasinBlock;
import org.dioptase.castingncrucibles.content.casting.basin.RefractoryBasinBlockItem;
import org.dioptase.castingncrucibles.content.casting.basin.RefractoryBasinGenerator;
import org.dioptase.castingncrucibles.content.casting.basin.RefractoryBasinModel;
import org.dioptase.castingncrucibles.foundation.data.recipe.OreTier;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.CreateRegistrate.casingConnectivity;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCBlocks {
    //BRICKS
    public static final BlockEntry<Block> CARBON_BRICKS = REGISTRATE.block("carbon_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.sound(SoundType.POLISHED_DEEPSLATE)
                    .mapColor(MapColor.COLOR_GRAY))
            .transform(pickaxeOnly())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> REFRACTORY_BRICKS = REGISTRATE.block("refractory_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.mapColor(MapColor.SAND))
            .transform(pickaxeOnly())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> FIRE_BRICKS = REGISTRATE.block("fire_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.sound(SoundType.ANCIENT_DEBRIS)
                    .mapColor(MapColor.TERRACOTTA_GRAY))
            .transform(pickaxeOnly())
            .simpleItem()
            .register();

    public static final BlockEntry<CasingBlock> STEEL_CASING = REGISTRATE.block("steel_casing", CasingBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY))
            .transform(pickaxeOnly())
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(CNCPartialModels.STEEL_CASING)))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, CNCPartialModels.STEEL_CASING)))
            .simpleItem()
            .register();

    public static final BlockEntry<RefractoryBasinBlock> REFRACTORY_BASIN = REGISTRATE.block("refractory_basin", RefractoryBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_GRAY)
                    .noOcclusion()
                    .forceSolidOn())
            .transform(pickaxeOnly())
            .blockstate(new RefractoryBasinGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> RefractoryBasinModel::new))
            .item(RefractoryBasinBlockItem::new)
            .properties(Item.Properties::fireResistant)
            .model(AssetLookup.customBlockItemModel("_", "block_single"))
            .build()
            .register();

    // ORES

    public static final Map<OreTier, BlockEntry<Block>>
            HEMATITE = registerOreFamily("hematite", CNCItems.HEMATITE_CHUNK),
            MAGNETITE = registerOreFamily("magnetite", CNCItems.MAGNETITE_CHUNK),
            CASSITERITE = registerOreFamily("cassiterite", CNCItems.CASSITERITE_CHUNK);

    public static Map<OreTier, BlockEntry<Block>> registerOreFamily(String baseName, Supplier<? extends Item> drop) {
        Map<OreTier, BlockEntry<Block>> map = new EnumMap<>(OreTier.class);
        for (OreTier tier : OreTier.values()) {
            map.put(tier, registerTieredOre(baseName, tier, drop));
        }
        return map;
    }

    public static BlockEntry<Block> registerTieredOre(
            String baseName,
            OreTier tier,
            Supplier<? extends Item> dropItem
    ) {
        String name = tier.getPrefix() + baseName + "_ore";

        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_ORE)
                .properties(p -> p.requiresCorrectToolForDrops().sound(SoundType.STONE))
                .transform(pickaxeOnly())
                .tag(tier.getHarvestTag())
                .tag(Tags.Blocks.ORES)
                .loot((lt, b) -> {
                    HolderLookup.RegistryLookup<Enchantment> enchantLookup =
                            lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);

                    lt.add(b, lt.createSilkTouchDispatchTable(b,
                            lt.applyExplosionDecay(b,
                                    LootItem.lootTableItem(dropItem.get())
                                            .apply(SetItemCountFunction.setCount(tier.getCountProvider()))
                                            .apply(ApplyBonusCount.addOreBonusCount(
                                                    enchantLookup.getOrThrow(Enchantments.FORTUNE)
                                            ))
                            )
                    ));
                })
                .simpleItem()
                .register();
    }

    public static void init() {

    }
}
