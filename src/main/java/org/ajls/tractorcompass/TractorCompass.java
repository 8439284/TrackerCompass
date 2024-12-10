package org.ajls.tractorcompass;

import org.bukkit.plugin.java.JavaPlugin;

import static org.ajls.tractorcompass.CompassItemStack.createCompass;

public final class TractorCompass extends JavaPlugin {
    public static TractorCompass plugin;
    public CompassItemStack compassItemStack;

    //sometimes with compass not in mainhand actionbar will flicker
    // you really need to fix the bug above(2024,12,10)

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        compassItemStack = new CompassItemStack();
        createCompass();
        getServer().getPluginManager().registerEvents(new MyListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
