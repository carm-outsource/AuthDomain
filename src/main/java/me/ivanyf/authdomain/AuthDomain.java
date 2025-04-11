package me.ivanyf.authdomain;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AuthDomain extends Plugin implements Listener {
    private File configFile;
    private Configuration configuration;
    private List<DomainPattern> whitelist;
    private String kickMessage;

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        loadConfig();
    }

    public void loadConfig() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                configuration.set("whitelist", new String[]{"example.com"});
                configuration.set("kick_message", "illegal connection!");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            whitelist = configuration.getStringList("whitelist").stream().map(DomainPattern::compile).collect(Collectors.toList());
            kickMessage = configuration.getString("kick_message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onHandShake(PlayerHandshakeEvent event) {
//        loadConfig();
        String address = event.getHandshake().getHost();
        PendingConnection connection = event.getConnection();
        if (!allowed(address)) {
            getLogger().info("BLOCKED CONNECTION: " + address);
            connection.disconnect(kickMessage);
        }
    }

    protected boolean allowed(String address) {
        return whitelist.stream().anyMatch(p -> p.matches(address));
    }
    
}
