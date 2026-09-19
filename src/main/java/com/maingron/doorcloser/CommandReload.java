package com.maingron.doorcloser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload implements CommandExecutor {
    private final DoorCloserPlugin _plugin;

    public CommandReload(DoorCloserPlugin plugin) {
        _plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Settings.Reload();

        if (sender instanceof Player) {
            Player player = (Player)sender;

            player.sendMessage("DoorCloser settings reloaded from config.yml.");
        }

        return true;
    }
}
