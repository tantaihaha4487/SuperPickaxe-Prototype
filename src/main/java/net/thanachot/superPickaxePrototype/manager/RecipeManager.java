package net.thanachot.superPickaxePrototype.manager;

import net.thanachot.superPickaxePrototype.SuperPickaxePrototype;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    public static void registerRecipes(SuperPickaxePrototype plugin) {
        ItemStack item = createSuperPickaxeItem();

        NamespacedKey key = SuperPickaxePrototype.getSuperPickaxeKey();
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape(
                "NNN",
                " S ",
                " S ");
        recipe.setIngredient('N', Material.NETHERITE_PICKAXE);
        recipe.setIngredient('S', Material.STICK);

        plugin.getServer().addRecipe(recipe);
        // Ensure the recipe appears in the player's recipe book (craftable list)
        plugin.getServer().getOnlinePlayers().forEach(player -> player.discoverRecipe(key));
    }

    private static ItemStack createSuperPickaxeItem() {
        ItemStack item = ItemStack.of(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(SuperPickaxePrototype.getSuperPickaxeKey(), PersistentDataType.BYTE, (byte) 1);

            meta.displayName(Component.text("Super Pickaxe", NamedTextColor.GOLD));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("A powerful pickaxe that can", NamedTextColor.GRAY));
            lore.add(Component.text("break multiple blocks at once.", NamedTextColor.GRAY));
            lore.add(Component.empty());
            meta.lore(lore);

            item.setItemMeta(meta);
        }
        return item;
    }
}
