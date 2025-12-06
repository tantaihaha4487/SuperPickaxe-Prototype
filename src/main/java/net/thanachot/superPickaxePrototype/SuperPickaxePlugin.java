package net.thanachot.superPickaxePrototype;

import net.thanachot.superPickaxePrototype.listener.BlockBreakListener;
import net.thanachot.superPickaxePrototype.listener.PlayerDeathListener;
import net.thanachot.superPickaxePrototype.listener.RecipeDiscoveryListener;
import net.thanachot.superPickaxePrototype.manager.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuperPickaxePlugin extends JavaPlugin {

    private static final String REQUIRED_SHIROCORE_VERSION = "2.0.0";
    private static final String SHIROCORE_PLUGIN_NAME = "ShiroCore";

    private static NamespacedKey superPickaxeKey;
    private static boolean shiroCoreEnabled = false;

    public static NamespacedKey getSuperPickaxeKey() {
        return superPickaxeKey;
    }

    /**
     * Check if ShiroCore integration is enabled
     */
    public static boolean isShiroCoreEnabled() {
        return shiroCoreEnabled;
    }

    @Override
    public void onEnable() {
        getLogger().info("SuperPickaxe plugin is enabling!");
        superPickaxeKey = new NamespacedKey(this, "superpickaxe");

        shiroCoreEnabled = initializeShiroCoreIntegration();
        registerListeners();
        registerRecipes();
        initializeMetrics();

        getLogger().info("SuperPickaxe plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        unregisterShiroCoreAbility();
        RecipeManager.unregisterRecipes(this);
        getLogger().info("SuperPickaxe plugin disabled.");
    }

    /**
     * Attempts to initialize ShiroCore integration.
     * 
     * @return true if integration was successful, false otherwise
     */
    private boolean initializeShiroCoreIntegration() {
        Plugin shiroCore = findShiroCorePlugin();

        if (shiroCore == null) {
            logShiroCoreNotFound();
            return false;
        }

        String version = shiroCore.getPluginMeta().getVersion();
        if (!isCompatibleVersion(version)) {
            logIncompatibleVersion(version);
            return false;
        }

        return registerShiroCoreAbility(version);
    }

    @Nullable
    private Plugin findShiroCorePlugin() {
        return getServer().getPluginManager().getPlugin(SHIROCORE_PLUGIN_NAME);
    }

    private boolean isCompatibleVersion(@NotNull String version) {
        return version.contains(REQUIRED_SHIROCORE_VERSION);
    }

    private boolean registerShiroCoreAbility(@NotNull String shiroCoreVersion) {
        try {
            return net.thanachot.superPickaxePrototype.integration.ShiroCoreIntegration
                    .registerAbility(getLogger(), shiroCoreVersion);
        } catch (NoClassDefFoundError e) {
            getLogger().warning("Failed to load ShiroCore integration: " + e.getMessage());
            return false;
        }
    }

    private void unregisterShiroCoreAbility() {
        if (shiroCoreEnabled) {
            try {
                net.thanachot.superPickaxePrototype.integration.ShiroCoreIntegration.unregisterAbility();
            } catch (NoClassDefFoundError ignored) {
                // ShiroCore classes not available - fail silently
            }
        }
    }

    private void logShiroCoreNotFound() {
        // Use try-catch in case shiro-api classes aren't available
        try {
            net.thanachot.shiroverse.api.util.DependencyLogger.logShiroCoreNotFound(
                    getLogger(),
                    "SuperPickaxe-Prototype",
                    REQUIRED_SHIROCORE_VERSION);
        } catch (NoClassDefFoundError e) {
            // Fallback to basic logging if DependencyLogger isn't available
            getLogger().warning("ShiroCore NOT FOUND! SuperPickaxe-Prototype requires ShiroCore v"
                    + REQUIRED_SHIROCORE_VERSION + "+ for abilities to work.");
            getLogger().warning("Download from: https://modrinth.com/plugin/shirocore");
        }
    }

    private void logIncompatibleVersion(@NotNull String foundVersion) {
        // Use try-catch in case shiro-api classes aren't available
        try {
            net.thanachot.shiroverse.api.util.DependencyLogger.logIncompatibleVersion(
                    getLogger(),
                    foundVersion,
                    REQUIRED_SHIROCORE_VERSION);
        } catch (NoClassDefFoundError e) {
            // Fallback to basic logging if DependencyLogger isn't available
            getLogger().warning("INCOMPATIBLE ShiroCore VERSION! Found: " + foundVersion
                    + ", Required: v" + REQUIRED_SHIROCORE_VERSION + "+");
            getLogger().warning("Download from: https://modrinth.com/plugin/shirocore");
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeDiscoveryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    private void registerRecipes() {
        RecipeManager.registerRecipes(this);
    }

    private void initializeMetrics() {
        // Only initialize metrics if ShiroCore is available (bstats is from shiro-api)
        if (!shiroCoreEnabled) {
            return;
        }

        try {
            new net.thanachot.shiroverse.api.bstats.Metrics(this, 28119);
        } catch (NoClassDefFoundError ignored) {
            // bstats not available - fail silently
        }
    }
}
