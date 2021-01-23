package smartcraft.tops.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import smartcraft.tops.NPCManager;
import smartcraft.tops.Tops;

public class PlayerJoinEvent implements Listener {

  public PlayerJoinEvent() {
    Tops plugin = Tops.getInstance();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(org.bukkit.event.player.PlayerJoinEvent e){
    NPCManager.create(e.getPlayer());
  }
}
