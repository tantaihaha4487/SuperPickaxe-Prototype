package net.thanachot.superPickaxePrototype.handler;

import net.thanachot.ShiroCore.api.ability.AbilityManager;
import net.thanachot.ShiroCore.api.ability.ShiftAbility;
import net.thanachot.superPickaxePrototype.manager.BlockManager;
import net.thanachot.superPickaxePrototype.utils.SuperPickaxeUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Simplified handler for SuperPickaxe 3x3 block breaking.
 * Uses the new AbilityManager API from ShiroCore.
 */
public class SuperPickaxeBlockHandler {

    private static final Set<String> activeBlocksBroken = new HashSet<>();

    private static String key(Block b) {
        return b.getWorld().getName() + ":" + b.getX() + ":" + b.getY() + ":" + b.getZ();
    }

    public static void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // Check if player has SuperPickaxe ability active
        Optional<AbilityManager> managerOpt = AbilityManager.get();
        if (managerOpt.isEmpty()) {
            return;
        }

        AbilityManager abilityManager = managerOpt.get();
        Optional<ShiftAbility> activeAbility = abilityManager.getActiveAbility(player);

        // Check if active ability is SuperPickaxe
        if (activeAbility.isEmpty() || !activeAbility.get().getId().equals("superpickaxe")) {
            return;
        }

        // Verify they're still holding a SuperPickaxe
        if (!SuperPickaxeUtils.isSuperPickaxe(heldItem)) {
            return;
        }

        Block center = event.getBlock();
        String centerKey = key(center);

        // Prevent infinite recursion
        if (activeBlocksBroken.remove(centerKey)) {
            return;
        }

        Vector face = BlockManager.getBreakingFace(player);
        int size = 3;
        List<Block> blocks = BlockManager.getAffectedBlocks(center, face, size);
        Material centerType = center.getType();

        for (Block b : blocks) {
            if (b.equals(center))
                continue;
            if (b.getType() == centerType) {
                activeBlocksBroken.add(key(b));
                player.breakBlock(b);
            }
        }
    }
}
