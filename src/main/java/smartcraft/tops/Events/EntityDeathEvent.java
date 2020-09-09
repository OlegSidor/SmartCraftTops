package smartcraft.tops.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import smartcraft.tops.Tops;

public class EntityDeathEvent implements Listener {

  private Tops plugin = Tops.getInstance();

  public EntityDeathEvent() {
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onDeath(org.bukkit.event.entity.EntityDeathEvent e) {
    if (plugin.getStats().get("monsters")) {
      if (e.getEntity().getKiller() != null) {
        if (e.getEntity() instanceof Monster) {
          Player player = e.getEntity().getKiller();
          plugin.setStat(player.getUniqueId().toString(), "monsters",
              plugin.getStat(player.getUniqueId().toString(), "monsters") + 1);
        }
      }
    }
  }

}
