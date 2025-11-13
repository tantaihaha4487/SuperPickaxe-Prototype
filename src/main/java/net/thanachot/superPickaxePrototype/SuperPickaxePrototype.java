package net.thanachot.superPickaxePrototype;

import net.thanachot.superPickaxePrototype.listener.BlockBreakListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperPickaxePrototype extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
