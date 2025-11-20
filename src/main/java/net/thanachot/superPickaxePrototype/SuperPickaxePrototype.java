package net.thanachot.superPickaxePrototype;

import net.thanachot.superPickaxePrototype.handler.RegisterHandler;
import net.thanachot.superPickaxePrototype.listener.BlockBreakListener;
import net.thanachot.superPickaxePrototype.listener.PlayerSwapItemListener;
import net.thanachot.superPickaxePrototype.listener.ShiftListener;
import org.bukkit.plugin.java.JavaPlugin;


public final class SuperPickaxePrototype extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SuperPickaxe plugin is enabling!");
        getServer().getPluginManager().registerEvents(new ShiftListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwapItemListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        RegisterHandler.register(this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("SuperPickaxe-Prototype has been disabled.");
    }
}
