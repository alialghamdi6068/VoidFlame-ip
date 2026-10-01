package net.voidflame.ip;

import io.papermc.paper.connection.PlayerConnection;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidFlameIpPlugin extends JavaPlugin {

    private String targetHost;
    private int targetPort;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadTarget();

        getLogger().info("VoidFlame-ip enabled.");
        getLogger().info("Transfer target: " + targetHost + ":" + targetPort);
    }

    private void loadTarget() {
        targetHost = getConfig().getString("server.target.host", "141.11.237.68").trim();
        targetPort = getConfig().getInt("server.target.port", 7817);
    }
}
