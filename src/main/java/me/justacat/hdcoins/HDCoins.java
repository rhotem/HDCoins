package me.justacat.hdcoins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.justacat.hdcoins.ClickEvent.createYAML;

public final class HDCoins extends JavaPlugin {

    public static List<Location> runningTasks = new ArrayList<>();

    @Override
    public void onEnable() {
        ClickEvent.dataFolder = getDataFolder();
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        getCommand("hdcoins").setExecutor(new HdCoinsCommand());
        getCommand("getTokenGen").setExecutor(new GetTokenGenCommand());
        getCommand("hdgive").setExecutor(new GiveCoinsCommand());
        refreshTasks();

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void refreshTasks() {
        File file = createYAML(getDataFolder(), "Blocks");

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        List<Location> list = (List<Location>) configuration.getList("locations", new ArrayList<>());

        for (Location location : list) {

            if (!runningTasks.contains(location)) {

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        if (!runningTasks.contains(location)) {
                            this.cancel();
                            return;
                        }

                        ItemStack item = new ItemStack(Material.SUNFLOWER);

                        ItemMeta meta = item.getItemMeta();

                        meta.setDisplayName(ChatColor.GOLD + "HD Coin");
                        meta.setLocalizedName("coin");

                        item.setItemMeta(meta);

                        location.getWorld().dropItem(location.add(0, 1, 0), item);
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(HDCoins.class), 600, 600);

                runningTasks.add(location);

            }
        }

    }
}