package net.thanachot.superPickaxePrototype.utils;

import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtils {

    public static boolean isSuperPickaxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        Byte value = meta.getPersistentDataContainer().get(
                SuperPickaxePlugin.getSuperPickaxeKey(),
                PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }
}
