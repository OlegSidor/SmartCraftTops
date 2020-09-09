package smartcraft.tops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import smartcraft.airdrop.AirDrop;
import smartcraft.tops.DB.Mysql;
import smartcraft.tops.Events.AirdropHarvestEvent;
import smartcraft.tops.Events.EntityDeathEvent;
import smartcraft.tops.Events.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Tops extends JavaPlugin {

  private static Tops instance;
  private Mysql mysql;
  private Economy economy = null;
  private Map<String, Boolean> stats = new HashMap<>();

  @Override
  public void onEnable() {
    instance = this;

    mysql = new Mysql();

    if (!new File(this.getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }


    if (!mysql.isConnected()) {
      Bukkit.getLogger().warning("Tops disabled, mysql problem");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    enableStats();

    if (!setupEconomy())
      stats.put("money", false);
    if (getServer().getServicesManager().getRegistration(AirDrop.class) != null)
      stats.put("airdrop", false);


    addEvents();

    getLogger().info("Tops enabled");
  }

  @Override
  public void onDisable() {
    getLogger().info("Tops disabled");
  }


  private void addEvents() {

    new EntityDeathEvent();
    new PlayerQuitEvent();
    if (stats.get("airdrop"))
      new AirdropHarvestEvent();

  }

  private void enableStats() {
    if (getConfig().contains("stats")) {
      getConfig().getStringList("stats").forEach(s -> stats.put(s, true));
    }
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


  private boolean setupEconomy() {
    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null) {
      economy = economyProvider.getProvider();
    }

    return (economy != null);
  }

  public Economy getEconomy() {
    return economy;
  }

  public Map<String, Boolean> getStats() {
    return stats;
  }
}
