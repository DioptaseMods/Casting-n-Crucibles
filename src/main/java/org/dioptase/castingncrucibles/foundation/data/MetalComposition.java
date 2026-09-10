package org.dioptase.castingncrucibles.foundation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record MetalComposition(Map<ResourceLocation, Float> metalParts, int color, float temperature) {

    public MetalComposition {
        metalParts = Map.copyOf(metalParts);
    }

    public static final Codec<MetalComposition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT)
                            .fieldOf("metalParts")
                            .forGetter(MetalComposition::metalParts),
                    Codec.INT.fieldOf("color").forGetter(MetalComposition::color),
                    Codec.FLOAT.fieldOf("temperature").forGetter(MetalComposition::temperature)
            ).apply(instance, MetalComposition::new)
    );

    public static final StreamCodec<ByteBuf, MetalComposition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
            MetalComposition::metalParts,
            ByteBufCodecs.INT,
            MetalComposition::color,
            ByteBufCodecs.FLOAT,
            MetalComposition::temperature,
            MetalComposition::new
    );
}
