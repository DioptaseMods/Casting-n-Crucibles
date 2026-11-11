package org.dioptase.castingncrucibles.registry;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.NORTH;

public class CNCShapes {
    public static final VoxelShaper
            REF_BASIN_CORNER = shape(0, 0, 0, 16, 16, 16).erase(0, 0, 2, 14, 16, 16).forHorizontal(NORTH),
            REF_BASIN_WALL = shape(0, 0, 0, 16, 16, 2).forHorizontal(NORTH),
            REF_BASIN_CORNER_BOTTOM = shape(0, 0, 0, 16, 16, 16).erase(0, 0, 2, 14, 16, 16).add(0, 0, 0, 16, 4, 16).forHorizontal(NORTH),
            REF_BASIN_WALL_BOTTOM = shape(0, 0, 0, 16, 16, 2).add(0, 0, 0, 16, 4, 16).forHorizontal(NORTH);

    public static final VoxelShape
            REF_BASIN_SINGLE = shape(0, 0, 0, 16, 16, 16).erase(2, 0, 2, 14, 16, 14).build(),
            REF_BASIN_SINGLE_BOTTOM = shape(0, 0, 0, 16, 16, 16).erase(2, 0, 2, 14, 16, 14).add(0, 0, 0, 16, 4, 16).build(),
            REF_BASIN_BOTTOM = shape(0, 0, 0, 16, 4, 16).build();

    public static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    public static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
