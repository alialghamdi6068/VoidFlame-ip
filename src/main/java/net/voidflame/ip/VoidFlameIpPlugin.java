package net.voidflame.ip;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class VoidFlameIpPlugin extends JavaPlugin implements Listener {

    private String cachedAddress;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        refreshCache();

        PluginCommand ip = Objects.requireNonNull(getCommand("ip"), "Command 'ip' missing from plugin.yml");
        ip.setExecutor(this);
        ip.setTabCompleter((sender, command, alias, args) ->
                sender.hasPermission("voidflame.ip.reload") && args.length == 1 && "reload".startsWith(args[0].toLowerCase())
                        ? List.of("reload")
                        : List.of());

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("VoidFlame-ip enabled. Custom address: " + cachedAddress);
    }

    private void refreshCache() {
        String address = getConfig().getString("server.address", "").trim();
        if (address.isEmpty()) {
            address = getConfig().getString("server.host", "play.VoidFlame.net").trim();
            int port = getConfig().getInt("server.port", 25565);
            if (port != 25565 && !address.contains(":")) {
                address += ":" + port;
            }
        }
        cachedAddress = address;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("ip")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("voidflame.ip.reload")) {
                sender.sendMessage(color(getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            reloadConfig();
            refreshCache();
            sender.sendMessage(color(getConfig().getString("messages.reloaded", "&aReloaded.")));
            return true;
        }

        if (!sender.hasPermission("voidflame.ip.use")) {
            sender.sendMessage(color(getConfig().getString("messages.no-permission", "&cNo permission.")));
            return true;
        }

        if (!getConfig().getBoolean("display.command.enabled", true)) return true;

        sendLines(sender, getConfig().getStringList("display.command.lines"));
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!getConfig().getBoolean("display.join-message.enabled", false)) return;
        sendLines(event.getPlayer(), getConfig().getStringList("display.join-message.lines"));
    }

    private void sendLines(CommandSender sender, List<String> lines) {
        for (String line : lines) {
            sender.sendMessage(color(applyPlaceholders(line)));
        }
    }

    private String applyPlaceholders(String text) {
        int port = getConfig().getInt("server.port", 25565);
        return text.replace("%ip%", cachedAddress)
                .replace("%host%", getConfig().getString("server.host", cachedAddress))
                .replace("%port%", String.valueOf(port));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
