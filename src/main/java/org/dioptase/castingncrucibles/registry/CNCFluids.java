package org.dioptase.castingncrucibles.registry;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.neoforged.neoforge.common.Tags;
import org.dioptase.castingncrucibles.CastingNCrucibles;
import org.dioptase.castingncrucibles.content.fluid.MetalFluid;
import org.dioptase.castingncrucibles.content.fluid.MetalFluidType;
import org.joml.Vector3f;

import java.util.Map;

import static org.dioptase.castingncrucibles.CastingNCrucibles.REGISTRATE;

public class CNCFluids {
    public static final FluidEntry<MetalFluid> MOLTEN_STEEL = REGISTRATE.metalFluid("molten_steel",
            MetalFluidType.create(0xFF98A4A8, new Vector3f(0.97f, 0.55f, 0.25f), 0.501f, Map.of()))
            .lang("Molten Steel")
            .bucket()
            .lang("Molten Steel Bucket")
            .tag(Tags.Items.BUCKETS)
            .build()
            .properties(b -> b.temperature(1152).density(2100).viscosity(2500).lightLevel(15))
            .register();

    public static final FluidEntry<MetalFluid> MOLTEN_IRON = REGISTRATE.metalFluid("molten_iron",
            MetalFluidType.create(0xFFFBDBC6, new Vector3f(0.97f, 0.55f, 0.25f), 0.501f, Map.of()))
            .lang("Molten Iron")
            .bucket()
            .lang("Molten Iron Bucket")
            .tag(Tags.Items.BUCKETS)
            .build()
            .properties(b -> b.temperature(1152).density(2100).viscosity(2500).lightLevel(15))
            .register();

    public static void init() {

    }
}
