package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.handler.MiningHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to handle block break events.
 * Delegates to MiningHandler for Super Pickaxe area mining logic.
 */
public class BlockBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        MiningHandler.handle(event);
    }
}
