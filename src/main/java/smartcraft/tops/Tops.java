package smartcraft.tops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import smartcraft.tops.DB.Mysql;
import smartcraft.tops.Events.EntityDeathEvent;

import java.io.File;

public final class Tops extends JavaPlugin {

  private static Tops instance;
  private Mysql mysql;


  @Override
  public void onEnable() {
    instance = this;

    mysql = new Mysql();

    if (!new File(this.getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }


    if (!mysql.isConnected()) {
      Bukkit.getLogger().warning("Plugin disabled");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }


    addEvents();

    getLogger().info("Tops enabled");
  }

  @Override
  public void onDisable() {
    getLogger().info("Tops disabled");
  }


  private void addEvents() {

    new EntityDeathEvent();

  }

  public void setStat(String UUID, String statName, int value) {
    String stats = mysql.getStats(UUID);
    JsonObject jsonObject = new JsonParser().parse("{}").getAsJsonObject();
    if (stats != null)
      jsonObject = new JsonParser().parse(stats).getAsJsonObject();

    jsonObject.addProperty(statName, value);
    if (stats == null) {
      mysql.createStats(UUID, jsonObject.toString());
    } else {
      mysql.changeStats(UUID, jsonObject.toString());
    }
  }

  public int getStat(String UUID, String statName) {
    String stats = mysql.getStats(UUID);
    if (stats != null) {
      JsonObject jsonObject = new JsonParser().parse(stats).getAsJsonObject();
      if (jsonObject.has(statName)) {
        return jsonObject.get(statName).getAsInt();
      }
    }
    return 0;
  }

  public static Tops getInstance() {
    return instance;
  }

  public Mysql getMysql() {
    return mysql;
  }
}
