package smartcraft.tops.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import smartcraft.tops.Tops;
import smartcraft.tops.tools.Economy;

public class PlayerQuitEvent implements Listener {

  private Tops plugin = Tops.getInstance();

  public PlayerQuitEvent() {
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(org.bukkit.event.player.PlayerQuitEvent e){
    if(plugin.getStats().get("money")){
      Economy.saveMoneyStat(e.getPlayer());
    }
  }
}
