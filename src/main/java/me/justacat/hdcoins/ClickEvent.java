package me.justacat.hdcoins;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClickEvent implements Listener {

    public static File dataFolder;


    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if (e.getItem() == null || !e.getItem().hasItemMeta()) return;

        if (e.getItem().getItemMeta().getLocalizedName().equals("coin")) {

            redeemCoins(e.getPlayer(), !e.getPlayer().isSneaking());

        }

    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {


        if (e.getItemInHand().getItemMeta() == null) return;

        if (e.getItemInHand().getItemMeta().getLocalizedName().equals("tokenGen")) {

            if (!e.isCancelled()) {

                Block block = e.getBlock();

                if (!isClean(block)) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Not enough place here!");
                    e.setCancelled(true);
                    return;
                }

                placeStructure(block.getLocation());

                block = block.getWorld().getBlockAt(block.getLocation().add(0, 4, 0));

                File file = createYAML(dataFolder, "Blocks");

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                List<Location> list = (List<Location>) configuration.getList("locations", new ArrayList<>());
                list.add(block.getLocation());

                configuration.set("locations", list);
                try {
                    configuration.save(file);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


                block.setMetadata("TokenGen", new FixedMetadataValue(JavaPlugin.getPlugin(HDCoins.class), true));
                JavaPlugin.getPlugin(HDCoins.class).refreshTasks();


            }


        }


    }


    private boolean isClean(Block block) {

        for (int x = -1; x <= 1; x++) {

            for (int z = -1; z <= 1; z++) {

                for (int y = 0; y < 6; y++) {

                    if (x != 0 && y != 0 && z != 0) {
                        if (block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z).getType() != Material.AIR) return false;
                    }


                }


            }

        }

        return true;
    }

    public void placeStructure(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        for (int addX = -1; addX <= 1; addX++) {

            for (int addZ = -1; addZ <= 1; addZ++) {

                for (int addY = 0; addY < 6; addY++) {

                    boolean corner = Math.abs(addX) == Math.abs(addZ);
                    switch (addY) {

                        case 0:
                            location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.GOLD_BLOCK);
                            break;
                        case 1:
                            if (corner) {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.STONE_BRICK_SLAB);

                            } else {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.BEDROCK);

                            }
                            break;
                        case 2:
                        case 3:
                            if (corner) {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.YELLOW_STAINED_GLASS_PANE);

                            } else {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.IRON_BARS);

                            }
                            break;
                        case 4:
                            if (corner) {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.STONE_BRICK_SLAB);

                            } else {
                                location.getWorld().getBlockAt(x + addX, y + addY, z + addZ).setType(Material.STONE_BRICKS);

                            }

                            break;

                    }
                    location.getWorld().getBlockAt(x, y + 2, z).setType(Material.END_PORTAL_FRAME);
                    location.getWorld().getBlockAt(x, y + 3, z).setType(Material.AIR);
                    location.getWorld().getBlockAt(x, y + 4, z).setType(Material.GOLD_BLOCK);


                }

            }
        }





    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("TokenGen")) return;

        e.setCancelled(true);
        for (int x = -1; x <= 1; x++) {

            for (int z = -1; z <= 1; z++) {

                for (int y = 0; y > -5; y--) {

                    e.getBlock().getWorld().getBlockAt(e.getBlock().getX() + x, e.getBlock().getY() + y, e.getBlock().getZ() + z).setType(Material.AIR);


                }


            }

        }

        ItemStack item = new ItemStack(Material.GOLD_BLOCK);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "HD Coins Generator");
        meta.setLocalizedName("tokenGen");

        item.setItemMeta(meta);

        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);

        File file = createYAML(dataFolder, "Blocks");

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        List<Location> list = (List<Location>) configuration.getList("locations", new ArrayList<>());
        list.remove(e.getBlock().getLocation());

        configuration.set("locations", list);
        try {
            configuration.save(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        HDCoins.runningTasks.remove(e.getBlock().getLocation());
        JavaPlugin.getPlugin(HDCoins.class).refreshTasks();





    }

    public static void redeemCoins(Player player, boolean onlyHand) {

        File file = createYAML(dataFolder, player.getUniqueId().toString());

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);



        int originalAmount = config.getInt("coins");


        int amountToAdd = 0;

        amountToAdd = amountToAdd + player.getInventory().getItemInMainHand().getAmount();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        if (!onlyHand) {

            ItemStack item = new ItemStack(Material.SUNFLOWER);

            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.GOLD + "HD Coin");
            meta.setLocalizedName("coin");

            item.setItemMeta(meta);

            for (int key : player.getInventory().all(Material.SUNFLOWER).keySet()) {
                ItemStack value = player.getInventory().all(Material.SUNFLOWER).get(key);


                if (value.isSimilar(item)) {

                    player.getInventory().setItem(key, new ItemStack(Material.AIR));
                    amountToAdd = amountToAdd + value.getAmount();

                }

            }


        }

        config.set("coins", amountToAdd + originalAmount);

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static File createYAML(File folder, String name) {

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Error while saving a file!");
            }
        }

        return file;
    }


}
