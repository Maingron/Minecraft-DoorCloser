package net.tenrem.doorcloser;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DoorCloserPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Settings.ThisPlugin = this;

        Settings.ReadConfigValues();

        // set up commands
        RegisterCommands();

        // register event listener
        RegisterEvents();
    }


    private void RegisterEvents() {
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
    }

    private void RegisterCommands() {
        getCommand("dcreload").setExecutor(new CommandReload(this));
    }

    @Override
    public void onDisable() {
        // removal of commands and events is done automatically by Bukkit/Spigot
        // runs all scheduled tasks to open/close doors
        Bukkit.getScheduler().getPendingTasks().forEach(worker -> {
            if (worker.getOwner().equals(this)) {
                ((Runnable) worker).run();
            }
        });
        //getLogger().info("onDisable has been invoked!");
    }
}
