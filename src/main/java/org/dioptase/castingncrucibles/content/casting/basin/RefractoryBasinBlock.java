package org.dioptase.castingncrucibles.content.casting.basin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RefractoryBasinBlock extends Block implements IWrenchable, IBE<RefractoryBasinBlockEntity> {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    public RefractoryBasinBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true));
    }

    public static boolean isRefractoryBasin(BlockState state) {
        return state.getBlock() instanceof RefractoryBasinBlock;
    }

    @Override
    public Class<RefractoryBasinBlockEntity> getBlockEntityClass() {
        return RefractoryBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefractoryBasinBlockEntity> getBlockEntityType() {
        return null;
    }
}
