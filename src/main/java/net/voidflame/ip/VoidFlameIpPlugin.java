package net.voidflame.ip;

import io.papermc.paper.connection.PlayerCommonConnection;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidFlameIpPlugin extends JavaPlugin implements Listener {

    private String targetHost;
    private int targetPort;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadTarget();
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("VoidFlame-ip enabled.");
        getLogger().info("Transfer target: " + targetHost + ":" + targetPort);
    }

    private void loadTarget() {
        targetHost = getConfig().getString("server.target.host", "141.11.237.68").trim();
        targetPort = getConfig().getInt("server.target.port", 7817);

        if (targetHost.isEmpty()) {
            throw new IllegalStateException("server.target.host cannot be empty");
        }
        if (targetPort < 1 || targetPort > 65535) {
            throw new IllegalStateException("server.target.port must be between 1 and 65535");
        }
    }

    @EventHandler
    public void onInitialConfigure(PlayerConnectionInitialConfigureEvent event) {
        PlayerCommonConnection connection = event.getConnection();

        // A transfer to the target server creates another connection event.
        // Do not transfer an already-transferred connection again.
        if (connection.isTransferred()) {
            return;
        }

        connection.transfer(targetHost, targetPort);
        getLogger().fine("Transferred connection to " + targetHost + ":" + targetPort);
    }
}
