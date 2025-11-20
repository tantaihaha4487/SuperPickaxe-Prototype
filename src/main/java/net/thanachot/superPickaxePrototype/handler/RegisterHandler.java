package net.thanachot.superPickaxePrototype.handler;

import net.thanachot.ShiroCore.api.ShiftActivation;
import net.thanachot.superPickaxePrototype.SuperPickaxePrototype;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class RegisterHandler {
    public static void register(SuperPickaxePrototype plugin) {
        List<Material> pickaxes = Arrays.asList(
                Material.WOODEN_PICKAXE,
                Material.STONE_PICKAXE,
                Material.IRON_PICKAXE,
                Material.GOLDEN_PICKAXE,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_PICKAXE
        );

        try {
            ShiftActivation shiftActivation = ShiftActivation.getOrThrow();
            shiftActivation.register(AbilityActivateHandler::activate, pickaxes.toArray(new Material[0]));
        } catch (IllegalStateException e) {
            plugin.getLogger().severe("ShiroCore's ShiftActivation service not found! " + e.getMessage());
            plugin.getLogger().severe("Disabling SuperPickaxePrototype.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }


    }
}
