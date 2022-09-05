package me.justacat.hdcoins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class HdCoinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {

            File file = ClickEvent.createYAML(ClickEvent.dataFolder, player.getUniqueId().toString());

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);



            player.sendMessage(ChatColor.GREEN + "You have " + config.getInt("coins") + " HD Coins!");

        }

        return true;
    }
}
