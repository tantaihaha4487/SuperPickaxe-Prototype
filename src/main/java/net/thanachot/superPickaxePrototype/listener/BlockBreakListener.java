package net.thanachot.superPickaxePrototype.listener;

import net.thanachot.superPickaxePrototype.handler.AbilityActivateHandler;
import net.thanachot.superPickaxePrototype.manager.BlockManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BlockBreakListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        AbilityActivateHandler.handle(event);
    }
}
