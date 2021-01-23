package smartcraft.tops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCManager {

  private static Tops plugin = Tops.getInstance();


  public static void create(Player p) {
    for (Map.Entry<String, Boolean> entry : plugin.getStats().entrySet()) {
      if (entry.getValue()) {
        create(entry.getKey(), p);
      }
    }
  }

  public static void create(String sName, Player player) {
    Map<String, Integer> top = plugin.getTop(sName);
    if (top == null) return;
    List<Location> locations = plugin.getMysql().getLocations(sName);
    if(locations.size() <= 0) return;
    MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
    int index = 0;
    for (Map.Entry<String, Integer> entry : top.entrySet()) {
      if(locations.size() < index) break;
      String locationS = locations.get(index).getBlockX()+""+ locations.get(index).getBlockY()+""+ locations.get(index).getBlockZ();
      PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      if(plugin.getNpcs().containsKey(locationS)){
        Bukkit.broadcastMessage("ASD");
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, plugin.getNpcs().get(locationS)));
        connection.sendPacket(new PacketPlayOutEntityDestroy(plugin.getNpcs().get(locationS).getId()));
      }
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
      GameProfile gameProfile = new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
      WorldServer nmsWorld = ((CraftWorld) locations.get(index).getWorld()).getHandle();
      EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
      Player npcPlayer = npc.getBukkitEntity().getPlayer();
      if (npcPlayer == null) return;
      npcPlayer.setPlayerListName("");
      npc.setLocation(locations.get(index).getX(), locations.get(index).getY(), locations.get(index).getZ(), locations.get(index).getYaw(), locations.get(index).getPitch());

      connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
      connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
      connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((locations.get(index).getYaw() * 256.0F) / 360.0F)));
      plugin.getNpcs().put(locationS, npc);
      index++;
    }
  }

}
