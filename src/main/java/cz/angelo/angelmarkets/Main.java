package cz.angelo.angelmarkets;

import cz.angelo.angelmarkets.commands.angelmarkets;
import cz.angelo.angelmarkets.configurations.Config;
import cz.angelo.angelmarkets.configurations.Markets;
import cz.angelo.angelmarkets.configurations.Messages;
import cz.angelo.angelmarkets.listeners.*;
import cz.angelo.angelmarkets.threads.Reload;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Main extends JavaPlugin {

    public static Main getInstance;
    public static Economy econ = null;

    @Override
    public void onEnable() {
        getInstance = this;
        this.getCommand("angelmarkets").setExecutor(new angelmarkets());
        tasksRegister();
        if (!setupEconomy() ) {
            System.out.println(String.format("Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.saveResource("config.yml", false);
        this.saveResource("markets.yml", false);
        this.saveResource("messages.yml", false);
        BukkitTask reload = new Reload(this).runTaskTimer(this, 0L, 20L);
        eventRegister();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void tasksRegister(){
        BukkitTask reload = new Reload(this).runTaskTimer(this, 0L, 20L);
    }

    public void eventRegister(){
        this.getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        this.getServer().getPluginManager().registerEvents(new Explosions(), this);
    }

}
