package net.thanachot.superPickaxePrototype.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.thanachot.ShiroCore.api.text.ActionbarMessage;
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
