package net.thanachot.superPickaxePrototype.ability;

import net.kyori.adventure.text.format.NamedTextColor;
import net.thanachot.ShiroCore.api.ability.ShiftAbility;
import net.thanachot.ShiroCore.api.text.ActionbarMessage;
import net.thanachot.superPickaxePrototype.utils.SuperPickaxeUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * SuperPickaxe ability that activates via shift-spam.
 * Provides 3x3 block breaking when active.
 */
public class SuperPickaxeAbility extends ShiftAbility {

    private final Set<UUID> activePlayers = new HashSet<>();

    public SuperPickaxeAbility() {
        super("superpickaxe", SuperPickaxeUtils::isSuperPickaxe);
    }

    @Override
    public void onActivate(@NotNull Player player, @NotNull ItemStack item) {
        activePlayers.add(player.getUniqueId());
        player.sendActionBar(ActionbarMessage.getAlert("Super Pickaxe Activated!", NamedTextColor.GREEN));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    @Override
    public void onDeactivate(@NotNull Player player) {
        activePlayers.remove(player.getUniqueId());
        player.sendActionBar(ActionbarMessage.getAlert("Super Pickaxe Deactivated!", NamedTextColor.RED));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
    }

    @Override
    public boolean isActive(@NotNull Player player) {
        return activePlayers.contains(player.getUniqueId());
    }
}
