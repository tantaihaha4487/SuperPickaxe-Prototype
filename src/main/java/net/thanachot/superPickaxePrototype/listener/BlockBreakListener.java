package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.handler.AbilityActivateHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        AbilityActivateHandler.handle(event);
    }
}
