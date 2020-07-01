package cz.angelo.angelmarkets.listeners;

import cz.angelo.angelmarkets.configurations.Markets;
import cz.angelo.angelmarkets.configurations.Messages;
import cz.angelo.angelmarkets.utils.Color;
import cz.angelo.angelmarkets.utils.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class PlayerInteract implements Listener {

    private Location loc1 = null;
    private Location loc2 = null;

    @EventHandler
    public void playerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
            ConfigurationSection markets = Markets.get().getConfigurationSection("markets");
            if (!markets.getKeys(false).isEmpty()) {
                for (String s : markets.getKeys(false)) {
                    double x1 = Markets.get().getDouble("markets." + s + ".region.loc1.x");
                    double y1 = Markets.get().getDouble("markets." + s + ".region.loc1.y");
                    double z1 = Markets.get().getDouble("markets." + s + ".region.loc1.z");
                    if (!Markets.get().contains("markets." + s + ".region.loc1.world")) {
                        Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: String 'region." + s + ".loc1.world' is empty");
                        break;
                    } else {
                        World w1 = Bukkit.getWorld(Markets.get().getString("markets." + s + ".region.loc1.world"));
                        if (Bukkit.getWorlds().contains(w1)) {
                            loc1 = new Location(w1, x1, y1, z1);
                        } else {
                            Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: Failed to find a world named " + w1.getName());
                            break;
                        }
                    }
                    if (!Markets.get().contains("markets." + s + ".region.loc2.world")) {
                        Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: String 'region." + s + ".loc2.world' is empty");
                        break;
                    } else {
                        double x2 = Markets.get().getDouble("markets." + s + ".region.loc2.x");
                        double y2 = Markets.get().getDouble("markets." + s + ".region.loc2.y");
                        double z2 = Markets.get().getDouble("markets." + s + ".region.loc2.z");
                        World w2 = Bukkit.getWorld(Markets.get().getString("markets." + s + ".region.loc2.world"));
                        if (Bukkit.getWorlds().contains(w2)) {
                            loc2 = new Location(w2, x2, y2, z2);
                        } else {
                            Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: Failed to find a world named " + w2.getName());
                            break;
                        }
                    }

                    if (loc1 != null || loc2 != null) {
                        Cuboid cuboid = new Cuboid(loc1, loc2);
                        if (cuboid.contains(b.getLocation())) {
                            e.setCancelled(true);
                            p.sendMessage(Color.color(Messages.get().getString("marketNoPermission")).replace("%market%", s));
                        }
                    } else {
                        Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: Failed to find a loc1/2 named");
                        break;
                    }

                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Warning: ConfigurationSection 'markets' in file 'markets.yml' is empty.");
            }
        }
    }

}
