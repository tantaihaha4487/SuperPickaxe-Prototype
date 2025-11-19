package net.thanachot.superPickaxePrototype.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ActivationManager {

    private static final Set<UUID> activatedPlayers = new HashSet<>();


    public static boolean isActivated(Player player) {
        return activatedPlayers.contains(player.getUniqueId());
    }

    public static void add(Player player) {
        activatedPlayers.add(player.getUniqueId());
    }

    public static void remove(Player player) {
        activatedPlayers.remove(player.getUniqueId());
    }

}
