package org.dioptase.castingncrucibles.content.casting.refractory_basin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class RefractoryBasinRenderer extends SafeBlockEntityRenderer<RefractoryBasinBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private final float capHeight = 1 / 4f;
    private final float tankHullWidth = 1 / 16f + 1 / 128f;
    private final float minPuddleHeight = 1 / 16f;

    public RefractoryBasinRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    protected void renderSafe(RefractoryBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;

        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        float level = fluidLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        FluidTank tank = be.tankInventory;
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        boolean top = fluidStack.getFluid()
                .getFluidType()
                .isLighterThanAir();

        float xMin = tankHullWidth;
        float xMax = xMin + be.getWidth() - 2 * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;

        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }

        float zMin = tankHullWidth;
        float zMax = zMin + be.getWidth() - 2 * tankHullWidth;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, bufferSource,
                ms, light, false, true);
        ms.popPose();
    }

    private void renderItems(RefractoryBasinBlockEntity basin, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (basin.getLevel() ==  null) return;
        ItemRenderer itemRenderer = context.getItemRenderer();

        ms.pushPose();
        ms.translate(basin.width * 0.5f, 0, basin.width * 0.5f);
    }
}
