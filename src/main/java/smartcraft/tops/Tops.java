package smartcraft.tops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import smartcraft.airdrop.AirDrop;
import smartcraft.tops.Commands.TopsCommand;
import smartcraft.tops.DB.Mysql;
import smartcraft.tops.Events.AirdropHarvestEvent;
import smartcraft.tops.Events.EntityDeathEvent;
import smartcraft.tops.Events.PlayerJoinEvent;
import smartcraft.tops.Events.PlayerQuitEvent;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class Tops extends JavaPlugin {

  private static Tops instance;
  private Mysql mysql;
  private Economy economy = null;
  private Map<String, Boolean> stats = new HashMap<>();
  private Map<String, EntityPlayer> npcs = new HashMap<>();

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


    getCommand("tops").setExecutor(new TopsCommand());
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
    new PlayerJoinEvent();
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

  public Map<String, Integer> getTop(String statName){
    ResultSet result = mysql.getStats();
    Map<String, Integer> top = new HashMap<>();
    try {
      while (result.next()){
        JsonObject jsonObject = new JsonParser().parse(result.getString(mysql.getColumn())).getAsJsonObject();
        if(jsonObject.has(statName)) {
          top.put(result.getString("UUID"), jsonObject.get(statName).getAsInt());
        }
      }

      return top.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    } catch (SQLException e){
      e.printStackTrace();
    }
    return null;
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

  public Mysql getMysql() {
    return mysql;
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

  public Map<String, EntityPlayer> getNpcs() {
    return npcs;
  }
}
