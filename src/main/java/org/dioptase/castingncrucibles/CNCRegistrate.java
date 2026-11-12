package org.dioptase.castingncrucibles;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraft.resources.ResourceLocation;
import org.dioptase.castingncrucibles.content.fluid.MetalFluid;

public class CNCRegistrate extends CreateRegistrate {
    private static final ResourceLocation METAL_FLOW = ResourceLocation.fromNamespaceAndPath(CastingNCrucibles.MOD_ID,"block/metal_flowing");
    private static final ResourceLocation METAL_STILL = ResourceLocation.fromNamespaceAndPath(CastingNCrucibles.MOD_ID,"block/metal_still");

    protected CNCRegistrate(String modid) {
        super(modid);
    }

    public static CNCRegistrate create(String modid) {
        return new CNCRegistrate(modid);
    }

    public FluidBuilder<MetalFluid, CNCRegistrate> metalFluid(String name, ResourceLocation still, ResourceLocation flow,
                                                                          FluidBuilder.FluidTypeFactory typeFactory) {
        return entry(name, c -> new VirtualFluidBuilder<>(this, this, name, c, still, flow, typeFactory,
                MetalFluid::createSource, MetalFluid::createFlowing));
    }

    public FluidBuilder<MetalFluid, CNCRegistrate> metalFluid(String name, FluidBuilder.FluidTypeFactory typeFactory) {
        return entry(name, c -> new VirtualFluidBuilder<>(this, this, name, c, METAL_STILL, METAL_FLOW, typeFactory,
                MetalFluid::createSource, MetalFluid::createFlowing));
    }
}
