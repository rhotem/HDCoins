package me.justacat.hdcoins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static me.justacat.hdcoins.ClickEvent.createYAML;
import static me.justacat.hdcoins.ClickEvent.dataFolder;

public class GiveCoinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 2) {

            Player player = Bukkit.getPlayer(args[0]);

            File file = createYAML(dataFolder, player.getUniqueId().toString());

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);



            int originalAmount = config.getInt("coins");

            config.set("coins", originalAmount + Integer.parseInt(args[1]));

            try {
                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;

        } else {
          return false;
        }

    }
}
