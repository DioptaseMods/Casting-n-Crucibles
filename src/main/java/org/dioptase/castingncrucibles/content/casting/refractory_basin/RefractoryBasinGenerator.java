package org.dioptase.castingncrucibles.content.casting.refractory_basin;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

public class RefractoryBasinGenerator extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Boolean top = state.getValue(RefractoryBasinBlock.TOP);
        Boolean bottom = state.getValue(RefractoryBasinBlock.BOTTOM);
        RefractoryBasinBlock.Shape shape = state.getValue(RefractoryBasinBlock.SHAPE);

        String modelName = getString(top, bottom, shape);

        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }

    private static @NotNull String getString(Boolean top, Boolean bottom, RefractoryBasinBlock.Shape shape) {
        String shapeName = "middle";
        if (top && bottom && !shape.equals(RefractoryBasinBlock.Shape.INNER))
            shapeName = "single";
        else if (top && !shape.equals(RefractoryBasinBlock.Shape.INNER))
            shapeName = "top";
        else if (bottom)
            shapeName = "bottom";

        return shapeName + (top && (shape.isWall() || shape.isCorner()) ? "_" + shape.getSerializedName() : "");
    }
}
