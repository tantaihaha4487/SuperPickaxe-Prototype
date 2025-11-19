package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.handler.AbilityActivateHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerSwapItemListener implements Listener {

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent event) {
        AbilityActivateHandler.deactivate(event);
    }
}
