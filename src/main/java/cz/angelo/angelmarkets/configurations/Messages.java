package cz.angelo.angelmarkets.configurations;

import cz.angelo.angelmarkets.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class Messages {

    public static FileConfiguration config = null;
    public static File file = null;


    public static void reload() {
        if (config == null){
            file = new File(Main.getInstance.getDataFolder(), "messages.yml");
        }
        config = YamlConfiguration.loadConfiguration(file);
        Reader defConfgiStream = null;
        try {
            defConfgiStream = new InputStreamReader(Main.getInstance.getResource("messages.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfgiStream != null){
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfgiStream);
            config.setDefaults(defConfig);
        }
    }

    public static void setUp(){
        if (!file.exists()){
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static FileConfiguration get() {
        if (config == null){
            reload();
        }
        return config;
    }

    public static void save(){
        if (config == null || file == null){
            return;
        }
        try {
            get().save(file);
        }catch (IOException e){
            Main.getInstance.getLogger().log(Level.SEVERE, "Could not save config to " + file, e);
        }
    }

}
