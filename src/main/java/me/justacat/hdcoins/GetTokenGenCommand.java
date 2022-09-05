package me.justacat.hdcoins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetTokenGenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        ItemStack item = new ItemStack(Material.GOLD_BLOCK);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "HD Coins Generator");
        meta.setLocalizedName("tokenGen");

        item.setItemMeta(meta);

        if (sender instanceof Player player) {
            player.getInventory().addItem(item);
        }

        return true;
    }
}
