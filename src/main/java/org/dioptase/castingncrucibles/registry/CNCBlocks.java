package org.dioptase.castingncrucibles.registry;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.CreateRegistrate.casingConnectivity;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCBlocks {
    public static final BlockEntry<Block> CARBON_BRICKS = REGISTRATE.block("carbon_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.sound(SoundType.POLISHED_DEEPSLATE)
                    .mapColor(MapColor.COLOR_GRAY))
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

    public static void init() {

    }
}
