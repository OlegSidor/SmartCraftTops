package smartcraft.tops.tools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import smartcraft.tops.Tops;

public class Economy {

  private static Tops plugin = Tops.getInstance();

  public static void saveMoneyStat(Player p){
    plugin.setStat(p.getUniqueId().toString(), "money", (int) plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())));
  }
}
