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

    private static final String ITEM_NAME_SUFFIX = "Super Pickaxe";
    private static final NamedTextColor NAME_COLOR = NamedTextColor.GOLD;
    private static final NamedTextColor LORE_COLOR = NamedTextColor.GRAY;

    private static final Material[] PICKAXE_MATERIALS = {
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.COPPER_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE
    };

    /**
     * Registers all Super Pickaxe crafting recipes with the server.
     * 
     * @param plugin the plugin instance
     */
    public static void registerRecipes(@NotNull SuperPickaxePlugin plugin) {
        for (Material material : PICKAXE_MATERIALS) {
            ItemStack superPickaxeItem = createSuperPickaxeItem(material);
            NamespacedKey key = getRecipeKey(plugin, material);
            ShapedRecipe recipe = createCraftingRecipe(key, material, superPickaxeItem);

            plugin.getServer().addRecipe(recipe);
        }

        // Ensure the recipes appear in players' recipe books
        plugin.getServer().getOnlinePlayers()
                .forEach(player -> discoverRecipes(player, plugin));
    }

    /**
     * Unregisters all Super Pickaxe recipes from the server.
     * 
     * @param plugin the plugin instance
     */
    public static void unregisterRecipes(@NotNull SuperPickaxePlugin plugin) {
        for (Material material : PICKAXE_MATERIALS) {
            NamespacedKey key = getRecipeKey(plugin, material);
            if (plugin.getServer().removeRecipe(key)) {
                plugin.getServer().getOnlinePlayers()
                        .forEach(player -> player.undiscoverRecipe(key));
            }
        }
        plugin.getLogger().info("SuperPickaxe recipes unregistered.");
    }

    /**
     * Discovers all Super Pickaxe recipes for a player.
     * 
     * @param player the player
     * @param plugin the plugin instance
     */
    public static void discoverRecipes(@NotNull org.bukkit.entity.Player player, @NotNull SuperPickaxePlugin plugin) {
        for (Material material : PICKAXE_MATERIALS) {
            player.discoverRecipe(getRecipeKey(plugin, material));
        }
    }

    /**
     * Gets the namespaced key for a specific pickaxe material recipe.
     */
    @NotNull
    private static NamespacedKey getRecipeKey(@NotNull SuperPickaxePlugin plugin, @NotNull Material material) {
        return new NamespacedKey(plugin, "super_pickaxe_" + material.name().toLowerCase());
    }

    /**
     * Creates the crafting recipe for a specific Super Pickaxe type.
     */
    @NotNull
    private static ShapedRecipe createCraftingRecipe(@NotNull NamespacedKey key, @NotNull Material material,
            @NotNull ItemStack resultItem) {
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);

        recipe.shape(
                "PPP",
                " S ",
                " S ");
        recipe.setIngredient('P', material);
        recipe.setIngredient('S', Material.STICK);

        return recipe;
    }

    /**
     * Creates a Super Pickaxe item of the given material with custom metadata.
     */
    @NotNull
    private static ItemStack createSuperPickaxeItem(@NotNull Material material) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            markAsSuperPickaxe(meta);
            setDisplayNameAndLore(meta, material);
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
    private static void setDisplayNameAndLore(@NotNull ItemMeta meta, @NotNull Material material) {
        String baseName = formatMaterialName(material);
        meta.displayName(Component.text(baseName + " " + ITEM_NAME_SUFFIX, NAME_COLOR));

        List<Component> lore = List.of(
                Component.text("A powerful pickaxe that can", LORE_COLOR),
                Component.text("break multiple blocks at once.", LORE_COLOR),
                Component.empty());
        meta.lore(lore);
    }

    /**
     * Formats a material name (e.g. WOODEN_PICKAXE -> Wooden).
     */
    @NotNull
    private static String formatMaterialName(@NotNull Material material) {
        String name = material.name().replace("_PICKAXE", "");
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
