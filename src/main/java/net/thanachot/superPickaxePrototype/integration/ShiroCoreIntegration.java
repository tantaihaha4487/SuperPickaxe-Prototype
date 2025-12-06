package net.thanachot.superPickaxePrototype.integration;

import net.thanachot.shiroverse.api.ability.AbilityManager;
import net.thanachot.superPickaxePrototype.ability.SuperPickaxeAbility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Handles integration with ShiroCore.
 * This class is only loaded when ShiroCore is confirmed to exist,
 * preventing NoClassDefFoundError when ShiroCore is missing.
 */
public class ShiroCoreIntegration {

    private static SuperPickaxeAbility ability;
    private static String abilityId;
    private static AbilityManager abilityManager;

    /**
     * Registers the SuperPickaxe ability with ShiroCore.
     *
     * @param logger           the plugin logger
     * @param shiroCoreVersion the ShiroCore version string
     * @return true if registration was successful, false otherwise
     */
    public static boolean registerAbility(@NotNull Logger logger, @NotNull String shiroCoreVersion) {
        try {
            abilityManager = AbilityManager.getOrThrow();
            ability = new SuperPickaxeAbility();
            abilityId = ability.getId();

            abilityManager.registerAbility(ability);
            logger.info("✓ Registered SuperPickaxe ability with ShiroCore v" + shiroCoreVersion);
            return true;
        } catch (IllegalStateException e) {
            logRegistrationError(logger, e);
            return false;
        }
    }

    /**
     * Unregisters the SuperPickaxe ability from ShiroCore.
     */
    public static void unregisterAbility() {
        if (abilityId != null && abilityManager != null) {
            AbilityManager.get().ifPresent(manager -> manager.unregisterAbility(abilityId));
        }
    }

    /**
     * Deactivates the ability for the specified player.
     * This is a convenience method that handles null checks internally.
     *
     * @param player the player whose ability should be deactivated
     * @return true if the ability was deactivated, false otherwise
     */
    public static boolean deactivatePlayerAbility(@NotNull Player player) {
        if (abilityManager == null || abilityId == null) {
            return false;
        }

        try {
            abilityManager.deactivateAbility(player, abilityId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the ability is active for a player.
     *
     * @param player the player to check
     * @return true if the ability is active, false otherwise
     */
    public static boolean isAbilityActive(@NotNull Player player) {
        return ability != null && ability.isActive(player);
    }

    /**
     * Gets the ability ID.
     *
     * @return the ability ID, or null if not registered
     */
    public static String getAbilityId() {
        return abilityId;
    }

    /**
     * Checks if the integration is properly initialized.
     *
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return ability != null && abilityManager != null && abilityId != null;
    }

    private static void logRegistrationError(@NotNull Logger logger, @NotNull Exception e) {
        logger.warning(e.toString());
        logger.warning("╔════════════════════════════════════════════════════════════╗");
        logger.warning("║  ShiroCore API ERROR!                                      ║");
        logger.warning("║  Failed to register SuperPickaxe ability.                  ║");
        logger.warning("║                                                            ║");
        logger.warning("║  The plugin will continue without ability features.        ║");
        logger.warning("║                                                            ║");
        logger.warning("║  This might be a compatibility issue.                      ║");
        logger.warning("║  Download latest versions:                                 ║");
        logger.warning("║  → ShiroCore: https://modrinth.com/plugin/shirocore        ║");
        logger.warning("║  → SuperPickaxe: https://modrinth.com/plugin/superpickaxe-prototype ║");
        logger.warning("╚════════════════════════════════════════════════════════════╝");
    }
}
