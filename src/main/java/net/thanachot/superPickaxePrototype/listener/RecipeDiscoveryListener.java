package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.SuperPickaxePrototype;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class RecipeDiscoveryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Ensure the player discovers the Super Pickaxe recipe so it appears in the
        // recipe book
        player.discoverRecipe(SuperPickaxePrototype.getSuperPickaxeKey());
    }
}
