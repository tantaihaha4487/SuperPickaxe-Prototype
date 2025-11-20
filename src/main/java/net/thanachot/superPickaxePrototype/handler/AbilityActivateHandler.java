package net.thanachot.superPickaxePrototype.handler;

import net.kyori.adventure.text.format.NamedTextColor;
import net.thanachot.ShiroCore.api.text.ActionbarMessage;
import net.thanachot.ShiroCore.event.ShiftActivationEvent;
import net.thanachot.superPickaxePrototype.manager.ActivationManager;
import net.thanachot.superPickaxePrototype.manager.BlockManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AbilityActivateHandler {

    // ใช้ set ป้องกันการเกิด loop recursion จากการ break บล็อกซ้ำ
    private static final Set<String> activeBlocksBroken = new HashSet<>();

    private static String key(Block b) {
        return b.getWorld().getName() + ":" + b.getX() + ":" + b.getY() + ":" + b.getZ();
    }

    public static void activate(ShiftActivationEvent event) {
        Player player = event.getPlayer();

        ActivationManager.add(player);
        player.sendActionBar(ActionbarMessage.getAlert("Super Pickaxe Activated!", NamedTextColor.GREEN));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    public static void deactivate(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (ActivationManager.isActivated(player)) {
            ActivationManager.remove(player);
            player.sendActionBar(ActionbarMessage.getAlert("Super Pickaxe Deactivated!", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
        }
    }


    public static void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!ActivationManager.isActivated(player)) return;

        Block center = event.getBlock();
        String centerKey = key(center);

        if (activeBlocksBroken.remove(centerKey)) {
            return;
        }

        // inside your BlockBreakListener.onBlockBreak(...)
        Vector face = BlockManager.getBreakingFace(player);

        int size = 3;
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
