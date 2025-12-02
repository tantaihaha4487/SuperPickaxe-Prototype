package net.thanachot.superPickaxePrototype;

import net.thanachot.shiroverse.api.ability.AbilityManager;
import net.thanachot.superPickaxePrototype.ability.SuperPickaxeAbility;
import net.thanachot.shiroverse.api.bstats.Metrics;
import net.thanachot.superPickaxePrototype.listener.BlockBreakListener;
import net.thanachot.superPickaxePrototype.listener.RecipeDiscoveryListener;
import net.thanachot.superPickaxePrototype.manager.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperPickaxePlugin extends JavaPlugin {

    private static NamespacedKey superPickaxeKey;
    private SuperPickaxeAbility superPickaxeAbility;

    public static NamespacedKey getSuperPickaxeKey() {
        return superPickaxeKey;
    }

    @Override
    public void onEnable() {
        getLogger().info("SuperPickaxe plugin is enabling!");

        superPickaxeKey = new NamespacedKey(this, "superpickaxe");

        if (!registerAbility()) {
            return;
        }

        registerListeners();
        registerRecipes();
        initMetrics();

        getLogger().info("SuperPickaxe plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (superPickaxeAbility != null) {
            AbilityManager.get().ifPresent(manager -> manager.unregisterAbility(superPickaxeAbility.getId()));
        }
        RecipeManager.unregisterRecipes(this);
        getLogger().info("SuperPickaxe plugin disabled.");
    }

    private boolean registerAbility() {
        Plugin shiroCore = getServer().getPluginManager().getPlugin("ShiroCore");
        // Check if ShiroCore is loaded
        if (shiroCore == null) {
            getLogger().severe("╔════════════════════════════════════════════════════════════╗");
            getLogger().severe("║  ShiroCore NOT FOUND!                                      ║");
            getLogger().severe("║  SuperPickaxe-Prototype requires ShiroCore v2.0.0+         ║");
            getLogger().severe("║                                                            ║");
            getLogger().severe("║  Download ShiroCore from:                                  ║");
            getLogger().severe("║  → https://modrinth.com/plugin/shirocore                   ║");
            getLogger().severe("╚════════════════════════════════════════════════════════════╝");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        // Check ShiroCore version
        String shiroCoreVersion = shiroCore.getPluginMeta().getVersion();
        if (!shiroCoreVersion.contains("2.0.0")) {
            getLogger().severe("╔════════════════════════════════════════════════════════════╗");
            getLogger().severe("║  INCOMPATIBLE ShiroCore VERSION!                           ║");
            getLogger().severe("║  Found: " + String.format("%-49s", shiroCoreVersion) + "║");
            getLogger().severe("║  Required: v2.0.0+                                         ║");
            getLogger().severe("║                                                            ║");
            getLogger().severe("║  Please update ShiroCore:                                  ║");
            getLogger().severe("║  → https://modrinth.com/plugin/shirocore                   ║");
            getLogger().severe("╚════════════════════════════════════════════════════════════╝");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        try {
            AbilityManager abilityManager = AbilityManager.getOrThrow();
            superPickaxeAbility = new SuperPickaxeAbility();
            abilityManager.registerAbility(superPickaxeAbility);
            getLogger().info("✓ Registered SuperPickaxe ability with ShiroCore v" + shiroCoreVersion);
            return true;
        } catch (IllegalStateException e) {
            getLogger().severe("╔════════════════════════════════════════════════════════════╗");
            getLogger().severe("║  ShiroCore API ERROR!                                      ║");
            getLogger().severe("║  Failed to register SuperPickaxe ability.                  ║");
            getLogger().severe("║                                                            ║");
            getLogger().severe("║  This might be a compatibility issue.                      ║");
            getLogger().severe("║  Download latest versions:                                 ║");
            getLogger().severe("║  → ShiroCore: https://modrinth.com/plugin/shirocore        ║");
            getLogger().severe("║  → SuperPickaxe: https://modrinth.com/plugin/superpickaxe-prototype ║");
            getLogger().severe("╚════════════════════════════════════════════════════════════╝");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeDiscoveryListener(), this);
    }

    private void registerRecipes() {
        RecipeManager.registerRecipes(this);
    }

    private void initMetrics() {
        new Metrics(this, 28119);
    }
}
