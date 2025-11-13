package net.thanachot.superPickaxePrototype.listener;

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

public class BlockBreakListener implements Listener {

    // ใช้ set ป้องกันการเกิด loop recursion จากการ break บล็อกซ้ำ
    private static final Set<String> activeBlocksBroken = new HashSet<>();

    private static String key(Block b) {
        return b.getWorld().getName() + ":" + b.getX() + ":" + b.getY() + ":" + b.getZ();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block center = event.getBlock();
        Player player = event.getPlayer();
        String centerKey = key(center);

        // ถ้าบล็อกนี้ถูกขุดโดยระบบเราเองแล้ว ให้ข้าม
        if (activeBlocksBroken.remove(centerKey)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.NETHERITE_PICKAXE) {
            return; // เฉพาะ Netherite Pickaxe เท่านั้น
        }

        // inside your BlockBreakListener.onBlockBreak(...)
        Vector face = BlockManager.getBreakingFace(player);
        if (face == null) face = new Vector(0, 1, 0);

        // use size = 5 for 5x5
        int size = 5;
        List<Block> blocks = BlockManager.getAffectedBlocks(center, face, size);
        Material centerType = center.getType();

        for (Block b : blocks) {
            if (b.equals(center)) continue;
            if (b.getType() == centerType) {
                activeBlocksBroken.add(key(b));
                // prefer player.breakBlock(b) on Paper so enchantments & durability apply
                player.breakBlock(b);
            }
        }

    }
}
