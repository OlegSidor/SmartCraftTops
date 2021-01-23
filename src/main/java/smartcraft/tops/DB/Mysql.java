package smartcraft.tops.DB;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import smartcraft.tops.Tops;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Mysql {

  private Tops plugin = Tops.getInstance();
  private Connection connection;
  private String host, db, user, password, column;
  private int port;
  private boolean connected = false;

  public Mysql() {
    host = plugin.getConfig().getString("host");
    port = plugin.getConfig().getInt("port");
    db = plugin.getConfig().getString("db");
    user = plugin.getConfig().getString("user");
    password = plugin.getConfig().getString("password");
    column = plugin.getConfig().getString("column");

    try {
      synchronized (this) {
        if (connection != null && !connection.isClosed()) {
          return;
        }
        Class.forName("com.mysql.jdbc.Driver");

        setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password));

        Bukkit.getLogger().info("Auction: MySQL Connected");
        connected = true;
      }
    } catch (SQLException | ClassNotFoundException e) {
      Bukkit.getLogger().warning("Can\'t connect to database!");
    }
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public void createStats(String UUID, String json){
    try {
      PreparedStatement statement = connection.prepareStatement("INSERT Tops(UUID,"+column+") VALUES (?, ?)");
      statement.setString(1, UUID);
      statement.setString(2, json);

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void changeStats(String UUID, String json){
    try {
      PreparedStatement statement = connection.prepareStatement("UPDATE Tops SET "+column+" = ? WHERE UUID = ?");
      statement.setString(1, json);
      statement.setString(2, UUID);

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ResultSet getStats() {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT UUID, "+column+" FROM Tops");
      return statement.executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  public String getStats(String UUID) {
    try {
    PreparedStatement statement = connection.prepareStatement("SELECT "+column+" FROM Tops WHERE UUID = ?");
    statement.setString(1, UUID);
    ResultSet results = statement.executeQuery();
    if(results.next()) {
      return results.getString(column);
    }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setLocation(String type, Location l, int place){
    try {
      PreparedStatement statement = connection.prepareStatement("INSERT TopsLocations(`type`, x, y, z, pitch, yaw, server, place, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      statement.setString(1, type);
      statement.setDouble(2, l.getX());
      statement.setDouble(3, l.getY());
      statement.setDouble(4, l.getZ());
      statement.setDouble(5, l.getPitch());
      statement.setDouble(6, l.getYaw());
      statement.setString(7, column);
      statement.setInt(8, place);
      statement.setString(9, l.getWorld().getName());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Location> getLocations(String type){
    List<Location> locations = new ArrayList<>();
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM TopsLocations WHERE type = ? AND server = ? ORDER BY place");
      statement.setString(1, type);
      statement.setString(2, column);
      ResultSet result = statement.executeQuery();
      while (result.next()){
        Location location = new Location(Bukkit.getWorld(result.getString("world")),
            result.getDouble("x"), result.getDouble("y"), result.getDouble("z"),
            result.getFloat("yaw"), result.getFloat("pitch"));
        locations.add(location);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locations;
  }

  public boolean isConnected() {
    return connected;
  }

  public String getColumn() {
    return column;
  }
}
