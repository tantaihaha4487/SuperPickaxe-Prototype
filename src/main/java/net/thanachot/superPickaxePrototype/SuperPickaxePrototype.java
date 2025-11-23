package net.thanachot.superPickaxePrototype;

import net.thanachot.ShiroCore.api.ability.AbilityManager;
import net.thanachot.superPickaxePrototype.ability.SuperPickaxeAbility;
import net.thanachot.superPickaxePrototype.listener.BlockBreakListener;
import net.thanachot.superPickaxePrototype.manager.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperPickaxePrototype extends JavaPlugin {

    private static NamespacedKey superPickaxeKey;
    private SuperPickaxeAbility superPickaxeAbility;

    public static NamespacedKey getSuperPickaxeKey() {
        return superPickaxeKey;
    }

    @Override
    public void onEnable() {
        getLogger().info("SuperPickaxe plugin is enabling!");

        superPickaxeKey = new NamespacedKey(this, "superpickaxe");

        // Register the SuperPickaxe ability with AbilityManager
        try {
            AbilityManager abilityManager = AbilityManager.getOrThrow();
            superPickaxeAbility = new SuperPickaxeAbility();
            abilityManager.registerAbility(superPickaxeAbility);
            getLogger().info("Successfully registered SuperPickaxe ability!");
        } catch (IllegalStateException e) {
            getLogger().severe("ShiroCore's AbilityManager not found! " + e.getMessage());
            getLogger().severe("Disabling SuperPickaxePrototype.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register block break listener for 3x3 functionality
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

        // Register crafting recipes
        RecipeManager.registerRecipes(this);
        // Register recipe discovery listener for new players
        getServer().getPluginManager()
                .registerEvents(new net.thanachot.superPickaxePrototype.listener.RecipeDiscoveryListener(), this);

        getLogger().info("SuperPickaxe-Prototype enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Unregister ability if it exists
        if (superPickaxeAbility != null) {
            AbilityManager.get().ifPresent(manager -> manager.unregisterAbility(superPickaxeAbility.getId()));
        }

        getLogger().info("SuperPickaxe-Prototype has been disabled.");
    }

}
