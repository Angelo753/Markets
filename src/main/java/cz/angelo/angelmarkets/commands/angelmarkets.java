package cz.angelo.angelmarkets.commands;

import cz.angelo.angelmarkets.Main;
import cz.angelo.angelmarkets.configurations.Markets;
import cz.angelo.angelmarkets.configurations.Messages;
import cz.angelo.angelmarkets.utils.Color;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class angelmarkets implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            OfflinePlayer op = p;
            if (args.length == 0){
                p.sendMessage(Color.color(Messages.get().getString("wrongUsage")));
            }else {
                if (args.length == 1){
                    //Zobrazi help zpravu
                    if (args[0].equalsIgnoreCase("help")){
                        List<String> help = Messages.get().getStringList("help");
                        for (String s : help){
                            p.sendMessage(Color.color(s).replace("%cmd%", cmd.getName()));
                        }
                    }
                    if (args[0].equalsIgnoreCase("list")){
                        ConfigurationSection markets = Markets.get().getConfigurationSection("markets");
                        for (String s : markets.getKeys(false)){
                            if (!s.isEmpty()){
                                p.sendMessage(Color.color(Messages.get().getString("marketList.title")));
                                p.sendMessage(Color.color(Messages.get().getString("marketList.format")).replace("%market%", s).replace("%price%", Markets.get().getString("markets." + s + ".price")).replace("%forsale%", String.valueOf(Markets.get().getBoolean("markets." + s + ".selling"))));
                            }else {
                                //Markety jsou prazdne
                                p.sendMessage(Color.color(Messages.get().getString("marketList.title")));
                                p.sendMessage(Color.color(Messages.get().getString("marketList.empty")));
                                break;
                            }
                        }
                    }
                }else {
                    if (args.length == 2){
                        if (args[0].equalsIgnoreCase("buy")){
                            ConfigurationSection markets = Markets.get().getConfigurationSection("markets");
                            for (String s : markets.getKeys(false)){
                                if (s.contains(args[1])){
                                    if (Markets.get().getBoolean("markets." + s + ".selling")){
                                        if (Markets.get().getInt("markets." + s + ".price") <= Main.econ.getBalance(p)){
                                            //Odebrani pozadovane castky
                                            Main.econ.withdrawPlayer(op, Markets.get().getInt("markets." + s + ".price"));
                                            //Uprava configu
                                            Markets.get().set("markets." + args[1] + ".selling", false);
                                            Markets.get().set("markets." + args[1] + ".owner", p.getName());
                                            Markets.save();
                                            //Broadcast
                                            Bukkit.broadcastMessage(Color.color(Messages.get().getString("marketBuy")).replace("%player%", p.getName()).replace("%market%", args[1]));
                                        }
                                    }else {
                                        //Market neni na prodej
                                        p.sendMessage(Color.color(Messages.get().getString("notForSale")));
                                    }
                                }else {
                                    //Market neexistuje
                                    p.sendMessage(Color.color(Messages.get().getString("marketDoesNotExist")));
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("set")) {
                            if (p.hasPermission("angelmarkets.command.set") || p.hasPermission("*") || p.isOp()) {
                                ConfigurationSection markets = Markets.get().getConfigurationSection("markets");
                                if (markets.contains(args[1])) {
                                    double x = p.getTargetBlock(null, 200).getLocation().getX();
                                    double y = p.getTargetBlock(null, 200).getLocation().getY();
                                    double z = p.getTargetBlock(null, 200).getLocation().getZ();
                                    String world = p.getTargetBlock(null, 200).getLocation().getWorld().getName();
                                    if (!Markets.get().contains("markets." + args[1] + ".region") || Markets.get().getString("markets." + args[1] + ".region").isEmpty()){
                                        Markets.get().set("markets." + args[1] + ".region.loc1.x", x);
                                        Markets.get().set("markets." + args[1] + ".region.loc1.y", y);
                                        Markets.get().set("markets." + args[1] + ".region.loc1.z", z);
                                        Markets.get().set("markets." + args[1] + ".region.loc1.world", world);
                                        Markets.save();
                                    }else {
                                        Markets.get().set("markets." + args[1] + ".region.loc2.x", x);
                                        Markets.get().set("markets." + args[1] + ".region.loc2.y", y);
                                        Markets.get().set("markets." + args[1] + ".region.loc2.z", z);
                                        Markets.get().set("markets." + args[1] + ".region.loc2.world", world);
                                        Markets.save();
                                    }
                                } else {
                                    p.sendMessage(Color.color(Messages.get().getString("marketDoesNotExist")));
                                }
                            }
                        }
                    }else {
                        if (args.length == 3){
                            if (args[0].equalsIgnoreCase("sell")){
                                ConfigurationSection markets = Markets.get().getConfigurationSection("markets");
                                for (String s : markets.getKeys(false)){
                                    if (s.contains(args[1])){
                                        if (Markets.get().getString("markets." + args[1] + ".owner").equalsIgnoreCase(p.getName())){
                                            //Uprava configu
                                            Markets.get().set("markets." + args[1] + ".price", args[2]);
                                            Markets.save();
                                            //Broadcast
                                            Bukkit.broadcastMessage(Color.color(Messages.get().getString("marketForSale")).replace("%player%", p.getName()).replace("%market%", s));
                                        }else {
                                            p.sendMessage(Color.color(Messages.get().getString("wrongOwner")));
                                        }
                                    }else {
                                        //Market neexistuje
                                        p.sendMessage(Color.color(Messages.get().getString("marketDoesNotExist")));
                                    }
                                }
                            }else {
                                //Argument na pozici 0 se nerovna "sell"
                                p.sendMessage(Color.color(Messages.get().getString("wrongUsage")));
                            }
                        }
                    }
                }
            }
        }else {
            sender.sendMessage(Color.color(Messages.get().getString("consoleError")));
        }
        return false;
    }
}
