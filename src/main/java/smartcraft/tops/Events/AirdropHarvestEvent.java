package smartcraft.tops.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import smartcraft.tops.Tops;

public class AirdropHarvestEvent implements Listener {
  private Tops plugin = Tops.getInstance();

  public AirdropHarvestEvent(){
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onAirDrop(smartcraft.airdrop.events.AirdropHarvestEvent e){
    if(plugin.getStats().get("airdrop")){
      plugin.setStat(e.getPlayer().getUniqueId().toString(), "airdrop",
          plugin.getStat(e.getPlayer().getUniqueId().toString(), "airdrop") + 1);
    }
  }
}
