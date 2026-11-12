package org.dioptase.castingncrucibles.content.fluid;

import com.simibubi.create.content.fluids.VirtualFluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.dioptase.castingncrucibles.foundation.data.MetalComposition;
import org.dioptase.castingncrucibles.registry.CNCDataComponents;

import java.util.Map;
import java.util.function.Supplier;

public class MetalFluid extends VirtualFluid {

    public static MetalFluid createSource(Properties properties) {
        return new MetalFluid(properties, true);
    }

    public static MetalFluid createFlowing(Properties properties) {
        return new MetalFluid(properties, false);
    }

    public MetalFluid(Properties properties, boolean source) {
        super(properties, source);
    }

    @Override
    public Item getBucket() {
        ResourceLocation key = BuiltInRegistries.FLUID.getKey(this);
        if (key != null) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath().replace("flowing_", "") + "_bucket"));
            if (item != Items.AIR) {
                return item;
            }
        }
        return super.getBucket();
    }

    public static FluidStack of(Fluid fluid, int amount) {
        FluidStack stack = new FluidStack(fluid, amount);
        int color = 0xFFFFFFFF;
        float temp = 300f;
        if (fluid.getFluidType() instanceof MetalFluidType metalType) {
            color = metalType.getTintColor();
            temp = metalType.getTemperature(stack);
        }
        stack.set(CNCDataComponents.METAL_COMPOSITION.get(), new MetalComposition(Map.of(), color, temp));
        return stack;
    }

    public static FluidStack of(Fluid fluid, int amount, MetalComposition composition) {
        FluidStack stack = new FluidStack(fluid, amount);
        setComposition(stack, composition);
        return stack;
    }

    public static FluidStack of(Supplier<? extends Fluid> fluid, int amount) {
        return of(fluid.get(), amount);
    }

    public static FluidStack of(Supplier<? extends Fluid> fluid, int amount, MetalComposition composition) {
        return of(fluid.get(), amount, composition);
    }

    public static FluidStack setComposition(FluidStack stack, MetalComposition composition) {
        if (composition == null) {
            stack.remove(CNCDataComponents.METAL_COMPOSITION.get());
            return stack;
        }
        stack.set(CNCDataComponents.METAL_COMPOSITION.get(), composition);
        return stack;
    }
}
