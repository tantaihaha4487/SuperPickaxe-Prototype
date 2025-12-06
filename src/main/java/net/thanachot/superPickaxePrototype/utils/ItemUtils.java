package net.thanachot.superPickaxePrototype.utils;

import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for item-related operations.
 */
public class ItemUtils {

    /**
     * Checks if the given item is a Super Pickaxe.
     * 
     * @param item the item to check (may be null)
     * @return true if the item is a Super Pickaxe, false otherwise
     */
    public static boolean isSuperPickaxe(@Nullable ItemStack item) {
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
