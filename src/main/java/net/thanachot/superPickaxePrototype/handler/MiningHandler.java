package net.thanachot.superPickaxePrototype.handler;

import net.thanachot.superPickaxePrototype.SuperPickaxePlugin;
import net.thanachot.superPickaxePrototype.integration.ShiroCoreIntegration;
import net.thanachot.superPickaxePrototype.manager.AreaMiningManager;
import net.thanachot.superPickaxePrototype.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the mining logic for the Super Pickaxe.
 */
public class MiningHandler {

    private static final Set<String> processingBlocks = new HashSet<>();

    public static void handle(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block centerBlock = event.getBlock();

        if (shouldSkipProcessing(player, centerBlock)) {
            return;
        }

        try {
            processAreaMining(player, centerBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if we should skip processing for this block break event.
     */
    private static boolean shouldSkipProcessing(@NotNull Player player, @NotNull Block block) {
        // Skip if already processing this block (prevents recursion)
        if (processingBlocks.contains(createBlockKey(block))) {
            return true;
        }

        // Skip if not using Super Pickaxe
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!ItemUtils.isSuperPickaxe(heldItem)) {
            return true;
        }

        // Skip if ShiroCore is not enabled
        if (!SuperPickaxePlugin.isShiroCoreEnabled()) {
            return true;
        }

        // Skip if ability is not active for this player
        try {
            return !ShiroCoreIntegration.isAbilityActive(player);
        } catch (NoClassDefFoundError e) {
            return true;
        }
    }

    private static void processAreaMining(@NotNull Player player, @NotNull Block centerBlock) {
        Vector breakingFace = AreaMiningManager.getBreakingFace(player);
        List<Block> affectedBlocks = AreaMiningManager.getAffectedBlocks(centerBlock, breakingFace, 3);
        Material targetMaterial = centerBlock.getType();

        for (Block block : affectedBlocks) {
            // Skip the center block (already broken by the event)
            if (block.equals(centerBlock)) {
                continue;
            }

            // Only break blocks of the same material type
            if (block.getType() == targetMaterial) {
                breakBlockSafely(player, block);
            }
        }
    }

    /**
     * Safely breaks a block with recursion prevention.
     */
    private static void breakBlockSafely(@NotNull Player player, @NotNull Block block) {
        String blockKey = createBlockKey(block);

        // Only process if not already being processed
        if (processingBlocks.add(blockKey)) {
            try {
                player.breakBlock(block);
            } finally {
                processingBlocks.remove(blockKey);
            }
        }
    }

    /**
     * Creates a unique key for a block based on its world and coordinates.
     */
    private static String createBlockKey(@NotNull Block block) {
        return block.getWorld().getUID() + ":" +
                block.getX() + "," +
                block.getY() + "," +
                block.getZ();
    }
}
