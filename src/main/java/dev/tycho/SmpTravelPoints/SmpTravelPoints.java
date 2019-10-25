package dev.tycho.SmpTravelPoints;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import dev.tycho.SmpTravelPoints.listener.StructureListener;
import dev.tycho.SmpTravelPoints.listener.TeleportListener;
import dev.tycho.SmpTravelPoints.model.CustomItems;
import dev.tycho.SmpTravelPoints.model.EnderDiamondRecipe;
import dev.tycho.SmpTravelPoints.model.TeleporterRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class SmpTravelPoints extends JavaPlugin {
    public static Dao<Teleporter, Integer> teleportDao;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);

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
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, username, password);

            teleportDao = DaoManager.createDao(connectionSource, Teleporter.class);
            TableUtils.createTableIfNotExists(connectionSource, Teleporter.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        CustomItems.init();

        //Listener registration
        getServer().getPluginManager().registerEvents(new StructureListener(), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(), this);
        getServer().getPluginManager().registerEvents(new TeleporterRecipe(this), this);
        EnderDiamondRecipe enderDiamondRecipe = new EnderDiamondRecipe(this);
    }
    @Override
    public void onDisable() {

    }
}
