package org.dioptase.castingncrucibles.registry;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.resources.ResourceLocation;
import org.dioptase.castingncrucibles.CastingNCrucibles;

public class CNCPartialModels {

    public static final CTSpriteShiftEntry STEEL_CASING = omni("steel_casing");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return com.simibubi.create.foundation.block.connected.CTSpriteShifter.getCT(type,
                ResourceLocation.fromNamespaceAndPath(CastingNCrucibles.MOD_ID, "block/" + blockTextureName),
                ResourceLocation.fromNamespaceAndPath(CastingNCrucibles.MOD_ID, "block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static void init() {
    }
}
