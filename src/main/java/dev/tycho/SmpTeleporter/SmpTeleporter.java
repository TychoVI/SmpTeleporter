package dev.tycho.SmpTeleporter;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.tycho.SmpTeleporter.Command.SetIconCommand;
import dev.tycho.SmpTeleporter.database.Teleporter;
import dev.tycho.SmpTeleporter.listener.SetIconListener;
import dev.tycho.SmpTeleporter.listener.StructureListener;
import dev.tycho.SmpTeleporter.listener.TeleportListener;
import dev.tycho.SmpTeleporter.util.CustomItems;
import dev.tycho.SmpTeleporter.model.EnderDiamondRecipe;
import dev.tycho.SmpTeleporter.model.TeleporterRecipe;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class SmpTeleporter extends JavaPlugin {
    public static Dao<Teleporter, Integer> teleportDao;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static InventoryManager inventoryManager;

    public static ArrayList<UUID> iconSetters = new ArrayList<>();

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        this.saveDefaultConfig();

//      Database initialization
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

        String host = getConfig().getString("mysql.host");
        String port = getConfig().getString("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String useSsl = getConfig().getString("mysql.ssl");

        String databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL=" + useSsl;

        try {
            ConnectionSource connectionSource = new JdbcPooledConnectionSource(databaseUrl, username, password);

            teleportDao = DaoManager.createDao(connectionSource, Teleporter.class);
            TableUtils.createTableIfNotExists(connectionSource, Teleporter.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        CustomItems.init();

        //Listener registration
        getServer().getPluginManager().registerEvents(new StructureListener(), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(), this);
        getServer().getPluginManager().registerEvents(new SetIconListener(), this);
        getServer().getPluginManager().registerEvents(new TeleporterRecipe(this), this);
        EnderDiamondRecipe enderDiamondRecipe = new EnderDiamondRecipe(this);

        this.getCommand("TpIcon").setExecutor(new SetIconCommand());
    }
    @Override
    public void onDisable() {

    }
}
