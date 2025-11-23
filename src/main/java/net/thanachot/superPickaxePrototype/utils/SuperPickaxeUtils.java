package net.thanachot.superPickaxePrototype.utils;

import net.thanachot.superPickaxePrototype.SuperPickaxePrototype;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SuperPickaxeUtils {

    public static boolean isSuperPickaxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        // Check for the specific value to ensure it's our crafted item
        Byte value = container.get(SuperPickaxePrototype.getSuperPickaxeKey(), PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

}
