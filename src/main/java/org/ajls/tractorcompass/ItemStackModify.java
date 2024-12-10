package org.ajls.tractorcompass;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackModify {
    //unbreakable
    public static ItemStack setUnbreakable(ItemStack itemStack){
        // set unbreakable
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, String lore) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> loresList = new ArrayList<String>();
        loresList.add(lore);
        meta.setLore(loresList);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack addLore(ItemStack itemStack, String lore) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> loresList = new ArrayList<>();
        if (meta.getLore() != null) {
            loresList = meta.getLore();
        }
        loresList.add(lore);
        meta.setLore(loresList);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setClassItem(ItemStack itemStack) {
        addLore(itemStack, "classItem");
        return itemStack;
    }

}
