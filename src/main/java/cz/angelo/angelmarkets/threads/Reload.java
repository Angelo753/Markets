package cz.angelo.angelmarkets.threads;

import cz.angelo.angelmarkets.Main;
import cz.angelo.angelmarkets.configurations.Config;
import cz.angelo.angelmarkets.configurations.Messages;
import org.bukkit.scheduler.BukkitRunnable;

public class Reload extends BukkitRunnable {


    Main plugin;

    public Reload(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Config.reload();
        Messages.reload();
    }
}
