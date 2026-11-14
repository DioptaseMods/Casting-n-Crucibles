package org.dioptase.castingncrucibles.foundation.utility;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class CNCUtils {

    //TFMG:CE was referenced for this!
    public static boolean createItemTooltip(List<Component> tooltip, IItemHandler itemHandler) {
        if (itemHandler == null) return false;

        if (itemHandler.getSlots() == 0) return false;

        CreateLang.translate("goggles.item_storage").forGoggles(tooltip);
        boolean isEmpty = true;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);

            if (itemStack.isEmpty()) continue;
            CreateLang.itemName(itemStack).style(ChatFormatting.GRAY).add(Component.literal(" x " + itemStack.getCount()).withStyle(ChatFormatting.DARK_GREEN)).forGoggles(tooltip, 1);
            isEmpty = false;
        }
        if (itemHandler.getSlots() > 1) {
            if (isEmpty) tooltip.removeLast();
            return true;
        }
        if (!isEmpty) return true;

        CreateLang.translate("item_attributes.shulker_level.empty").style(ChatFormatting.DARK_GRAY).forGoggles(tooltip, 1);
        return true;
    }
}
