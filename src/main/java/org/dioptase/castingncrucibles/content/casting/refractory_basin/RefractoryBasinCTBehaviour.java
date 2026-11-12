package org.dioptase.castingncrucibles.content.casting.refractory_basin;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RefractoryBasinCTBehaviour extends ConnectedTextureBehaviour.Base {
    protected CTSpriteShiftEntry bottomShift;
    protected CTSpriteShiftEntry layerShift;

    public RefractoryBasinCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry bottomShift) {
        this.layerShift = layerShift;
        this.bottomShift = bottomShift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (sprite != null && direction.getAxis() == Direction.Axis.Y && bottomShift.getOriginal() == sprite) {
            return bottomShift;
        }
        return direction.getAxis().isHorizontal() ? layerShift : bottomShift;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        if (face.getAxis().isVertical() && otherPos.getY() != pos.getY())
            return false;
        return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }
}
