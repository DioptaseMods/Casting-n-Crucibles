package org.dioptase.castingncrucibles.content.casting.basin;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.dioptase.castingncrucibles.registry.CNCSpriteShifts;

import java.util.Arrays;

public class RefractoryBasinModel extends CTModel {

    //CrucibleModel.java from Create-Metallurgy was followed for this model!
    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();
    public RefractoryBasinModel(BakedModel originalModel) {
        super(originalModel, new RefractoryBasinCTBehaviour(CNCSpriteShifts.REFRACTORY_BASIN, CNCSpriteShifts.REFRACTORY_BASIN_BOTTOM, CNCSpriteShifts.REFRACTORY_BASIN_INNER));
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        for (Direction direction : Iterate.horizontalDirections)
            cullData.setCulled(direction, ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
        return builder.with(CULL_PROPERTY, cullData);
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis().isVertical()) return;
            culledFaces[face.get2DDataValue()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis().isVertical()) return false;
            return culledFaces[face.get2DDataValue()];
        }
    }
}
