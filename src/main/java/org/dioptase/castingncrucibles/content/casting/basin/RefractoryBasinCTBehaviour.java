package org.dioptase.castingncrucibles.content.casting.basin;

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
    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry innerShift;
    protected CTSpriteShiftEntry layerShift;

    public RefractoryBasinCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry bottomShift, CTSpriteShiftEntry innerShift) {
        this.layerShift = layerShift;
        this.topShift = bottomShift;
        this.innerShift = innerShift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (sprite != null && direction.getAxis() == Direction.Axis.Y && innerShift.getOriginal() == sprite) {
            return innerShift;
        }
        return direction.getAxis().isHorizontal() ? layerShift : topShift;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }
}
