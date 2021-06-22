package me.bryangaming.chatlab.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BukkitUtils {



    public static List<String> convertItemDataInText(ItemStack itemStack){

        List<String> itemList = new ArrayList<>();

        if (!itemStack.hasItemMeta()){
            itemList.add(itemStack.getType().name());

            for (Enchantment enchantment : itemStack.getEnchantments().keySet()){
                itemList.add(enchantment.getKey().getKey() + " " + itemStack.getEnchantments().get(enchantment));
            }
            return itemList;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasDisplayName()){
            itemList.add(itemMeta.getDisplayName());
        }else{
            itemList.add(itemStack.getType().name());
        }

        if (itemMeta.hasLore()){
            itemList.addAll(itemMeta.getLore());

        }

        if (!itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)){

            for (Enchantment enchantment : itemStack.getEnchantments().keySet()){
                itemList.add(enchantment.getKey().getKey() + " " + itemStack.getEnchantments().get(enchantment));
            }
        }

        return itemList;


    }
}
