package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import net.thanachot.superPickaxePrototype.integration.ShiroCoreIntegration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to handle player death events.
 * Ensures all abilities are deactivated when a player dies.
 */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        // Only process if ShiroCore is enabled
        if (!SuperPickaxePlugin.isShiroCoreEnabled()) {
            return;
        }

        Player player = event.getEntity();

        // Deactivate the ability for the player when they die
        try {
            ShiroCoreIntegration.deactivatePlayerAbility(player);
        } catch (NoClassDefFoundError ignored) {
            // ShiroCore classes not available - fail silently
        }
    }
}
