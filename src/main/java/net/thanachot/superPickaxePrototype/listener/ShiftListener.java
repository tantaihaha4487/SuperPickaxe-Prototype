package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.ShiroCore.event.ShiftProgressEvent;
import net.thanachot.superPickaxePrototype.manager.ActivationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShiftListener implements Listener {

    @EventHandler
    public void onShiftProgress(ShiftProgressEvent event) {
        if (ActivationManager.isActivated(event.getPlayer())) return;
    }

}
