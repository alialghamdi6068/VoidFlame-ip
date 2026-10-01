package net.voidflame.ip;

import org.bukkit.plugin.java.JavaPlugin;

public final class VoidFlameIpPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("VoidFlame-ip enabled. Connection target: "
                + getConfig().getString("server.target.host", "141.11.237.68")
                + ":" + getConfig().getInt("server.target.port", 7817));
    }
}
