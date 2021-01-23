package smartcraft.tops.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import smartcraft.tops.NPCManager;
import smartcraft.tops.Tops;

public class TopsCommand implements CommandExecutor {

  private Tops plugin = Tops.getInstance();


  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
    if(sender instanceof Player){
      if(args.length > 2){
        if(args[0].equalsIgnoreCase("setlocation")){
          plugin.getMysql().setLocation(args[1], ((Player) sender).getLocation(), Integer.parseInt(args[2]));
          sender.sendMessage(ChatColor.GREEN+"Локация для "+args[1]+" добевлена");
        }
      } else if (args.length > 1){
        if(args[0].equalsIgnoreCase("settitle")){

        }
      }
    } else sender.sendMessage("Only for players!");
    return true;
  }
}
