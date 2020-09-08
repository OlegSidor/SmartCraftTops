package smartcraft.tops;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Tops extends JavaPlugin {

  @Override
  public void onEnable() {

    if (!new File(this.getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }

    getLogger().info("Tops enabled");
  }

  @Override
  public void onDisable() {
    getLogger().info("Tops disabled");
  }
}
