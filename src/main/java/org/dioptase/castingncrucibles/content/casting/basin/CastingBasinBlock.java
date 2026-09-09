package org.dioptase.castingncrucibles.content.casting.basin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CastingBasinBlock extends Block implements IWrenchable, IBE<CastingBasinBlockEntity> {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    public CastingBasinBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true));
    }

    public static boolean isCastingBasin(BlockState state) {
        return state.getBlock() instanceof CastingBasinBlock;
    }

    @Override
    public Class<CastingBasinBlockEntity> getBlockEntityClass() {
        return CastingBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return null;
    }
}
