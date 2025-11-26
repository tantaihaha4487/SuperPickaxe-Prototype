package net.thanachot.superPickaxePrototype.handler;

import net.thanachot.shiroverse.api.ability.AbilityManager;
import net.thanachot.superPickaxePrototype.manager.AreaMiningManager;
import net.thanachot.superPickaxePrototype.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the mining logic for the Super Pickaxe.
 */
public class MiningHandler {

    private static final Set<String> processingBlocks = new HashSet<>();

    public static void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block centerBlock = event.getBlock();

        // Basic checks
        if (shouldIgnore(player)) {
            return;
        }

        String blockKey = getBlockKey(centerBlock);
        // If we are already processing this block (triggered by ourselves), ignore to
        // prevent recursion
        if (processingBlocks.contains(blockKey)) {
            return;
        }

        try {
            // Mark this block as being processed (though for the center block, it's already
            // broken by the event)
            // This is mainly to establish the context if needed, but primarily we need to
            // mark the *other* blocks.
            // Actually, for the center block, the event is already happening.

            processAreaMining(player, centerBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldIgnore(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemUtils.isSuperPickaxe(item)) {
            return true;
        }

        return AbilityManager.get()
                .flatMap(manager -> manager.getActiveAbility(player))
                .filter(ability -> "superpickaxe".equals(ability.getId()))
                .isEmpty();
    }

    private static void processAreaMining(Player player, Block center) {
        Vector face = AreaMiningManager.getBreakingFace(player);
        List<Block> blocks = AreaMiningManager.getAffectedBlocks(center, face, 3);
        Material targetType = center.getType();

        for (Block block : blocks) {
            if (block.equals(center))
                continue;

            // Only break blocks of the same type
            if (block.getType() == targetType) {
                String key = getBlockKey(block);
                // Mark as processing so the recursive event is ignored
                if (processingBlocks.add(key)) {
                    try {
                        player.breakBlock(block);
                    } finally {
                        processingBlocks.remove(key);
                    }
                }
            }
        }
    }

    private static String getBlockKey(Block block) {
        return block.getWorld().getUID() + ":" + block.getX() + "," + block.getY() + "," + block.getZ();
    }
}
