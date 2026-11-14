package org.dioptase.castingncrucibles.content.casting.refractory_basin.basin.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.dioptase.castingncrucibles.content.casting.refractory_basin.RefractoryBasinBlockEntity;

public class MeltingRecipe implements Recipe<MeltingRecipeParams> {
    @Override
    public boolean matches(MeltingRecipeParams meltingRecipeParams, Level level) {
        return false;
    }

    public static boolean matches(RefractoryBasinBlockEntity be, Recipe<?> recipe, IItemHandler slot) {
        return false;
    }

    @Override
    public ItemStack assemble(MeltingRecipeParams meltingRecipeParams, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
