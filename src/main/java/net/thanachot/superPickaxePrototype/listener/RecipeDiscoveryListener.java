package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import net.thanachot.superPickaxePrototype.manager.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to handle player join events.
 * Ensures players discover the Super Pickaxe recipe.
 */
public class RecipeDiscoveryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Ensure the player discovers all Super Pickaxe recipes so they appear in the
        // recipe book
        SuperPickaxePlugin plugin = SuperPickaxePlugin.getPlugin(SuperPickaxePlugin.class);
        RecipeManager.discoverRecipes(player, plugin);
    }
}
