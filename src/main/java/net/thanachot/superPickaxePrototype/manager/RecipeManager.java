package net.thanachot.superPickaxePrototype.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manager for handling Super Pickaxe recipe registration and item creation.
 */
public class RecipeManager {

    private static final String ITEM_NAME = "Super Pickaxe";
    private static final NamedTextColor NAME_COLOR = NamedTextColor.GOLD;
    private static final NamedTextColor LORE_COLOR = NamedTextColor.GRAY;

    /**
     * Registers the Super Pickaxe crafting recipe with the server.
     * 
     * @param plugin the plugin instance
     */
    public static void registerRecipes(@NotNull SuperPickaxePlugin plugin) {
        ItemStack superPickaxeItem = createSuperPickaxeItem();
        ShapedRecipe recipe = createCraftingRecipe(superPickaxeItem);

        plugin.getServer().addRecipe(recipe);

        // Ensure the recipe appears in players' recipe books
        plugin.getServer().getOnlinePlayers()
                .forEach(player -> player.discoverRecipe(SuperPickaxePlugin.getSuperPickaxeKey()));
    }

    /**
     * Unregisters the Super Pickaxe recipe from the server.
     * 
     * @param plugin the plugin instance
     */
    public static void unregisterRecipes(@NotNull SuperPickaxePlugin plugin) {
        NamespacedKey key = SuperPickaxePlugin.getSuperPickaxeKey();

        if (plugin.getServer().removeRecipe(key)) {
            plugin.getLogger().info("SuperPickaxe recipe unregistered.");
            plugin.getServer().getOnlinePlayers()
                    .forEach(player -> player.undiscoverRecipe(key));
        }
    }

    /**
     * Creates the crafting recipe for the Super Pickaxe.
     */
    @NotNull
    private static ShapedRecipe createCraftingRecipe(@NotNull ItemStack resultItem) {
        NamespacedKey key = SuperPickaxePlugin.getSuperPickaxeKey();
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);

        recipe.shape(
                "NNN",
                " S ",
                " S ");
        recipe.setIngredient('N', Material.NETHERITE_PICKAXE);
        recipe.setIngredient('S', Material.STICK);

        return recipe;
    }

    /**
     * Creates a Super Pickaxe item with custom metadata.
     */
    @NotNull
    private static ItemStack createSuperPickaxeItem() {
        ItemStack item = ItemStack.of(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            markAsSuperPickaxe(meta);
            setDisplayNameAndLore(meta);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Marks an item's metadata as a Super Pickaxe using persistent data.
     */
    private static void markAsSuperPickaxe(@NotNull ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(
                SuperPickaxePlugin.getSuperPickaxeKey(),
                PersistentDataType.BYTE,
                (byte) 1);
    }

    /**
     * Sets the display name and lore for the Super Pickaxe item.
     */
    private static void setDisplayNameAndLore(@NotNull ItemMeta meta) {
        meta.displayName(Component.text(ITEM_NAME, NAME_COLOR));

        List<Component> lore = List.of(
                Component.text("A powerful pickaxe that can", LORE_COLOR),
                Component.text("break multiple blocks at once.", LORE_COLOR),
                Component.empty());
        meta.lore(lore);
    }
}
