package org.dioptase.castingncrucibles.content.fluid;

import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.Map;
import java.util.function.Consumer;

import static org.dioptase.castingncrucibles.registry.CNCDataComponents.METAL_COMPOSITION;

public class MetalFluidType extends FluidType {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final int tintColor;
    private final Vector3f fogColor;
    private final float specificHeat;
    private final Map<ResourceLocation, Float> metalComposition;

    public MetalFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture,
                          int tintColor, Vector3f fogColor, float specificHeat,
                          Map<ResourceLocation, Float> metalComposition) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.tintColor = (tintColor & 0xFF000000) == 0 ? 0xFF000000 | tintColor : tintColor;
        this.fogColor = fogColor;
        this.specificHeat = specificHeat;
        this.metalComposition = metalComposition;
    }

    public static FluidBuilder.FluidTypeFactory create(int tintColor, Vector3f fogColor, float specificHeat, Map<ResourceLocation, Float> metalComposition) {
        return (properties, still, flow) -> new MetalFluidType(properties, still, flow,  tintColor, fogColor, specificHeat, metalComposition);
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public int getTintColor() {
        return tintColor;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    public float getSpecificHeat() {
        return specificHeat;
    }

    public Map<ResourceLocation, Float> getMetalComposition() {
        return metalComposition;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }

            @Override
            public int getTintColor(net.neoforged.neoforge.fluids.FluidStack stack) {
                if (stack.has(METAL_COMPOSITION.get())) {
                    return stack.get(METAL_COMPOSITION.get()).color();
                }
                return tintColor;
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return fogColor != null ? fogColor : fluidFogColor;
            }
        });
    }

    // This will need to be modified to use the METAL_COMPOSITION

    public void joinMetalFluid(MetalFluidType other) {
        // Loop through all elements in this metal
        for (Map.Entry<ResourceLocation, Float> entry : metalComposition.entrySet()) {
            // If the other metal contains the current element, then add and half the content values
            if (other.metalComposition.containsKey(entry.getKey()))
                metalComposition.put(entry.getKey(), (entry.getValue() + other.metalComposition.get(entry.getKey())) / 2.0f);
            // If it doesn't, then just half what's already in this metal
            else
                metalComposition.put(entry.getKey(), entry.getValue() / 2.0f);
        }
        // Loop through all elements in the other metal
        for (Map.Entry<ResourceLocation, Float> entry : other.metalComposition.entrySet()) {
            // If this metal doesn't contain the current element, then half the value of the current element from the other metal and add it to this metal
            if (!(metalComposition.containsKey(entry.getKey())))
                metalComposition.put(entry.getKey(), entry.getValue() / 2.0f);
        }
    }
}
