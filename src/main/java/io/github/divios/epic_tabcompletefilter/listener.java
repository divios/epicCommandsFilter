package io.github.divios.epic_tabcompletefilter;

import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class listener implements Listener {

    private static listener instance = null;
    private static final EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    public static Collection<String> commands = null;
    private static databaseManager dbManager = databaseManager.getInstance();

    public static listener getInstance() {
        if(instance == null) init();
        return instance;
    }

    public static void init() {
        instance = new listener();
        register();
    }

    public static void register() {
        Bukkit.getPluginManager().registerEvents(getInstance(), main);
    }

    public static void unregister() {
        PlayerCommandSendEvent.getHandlerList().unregister(getInstance());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommandSend(PlayerCommandSendEvent e) {

        Collection<String> newCommands = new ArrayList<>();

        Player p = e.getPlayer();
        if(p.isOp()) return;
        for(Map.Entry<String, List<String>> entry: dbManager.getFilters().entrySet()) {
            if(!p.hasPermission("etcf." + entry.getKey())) continue;
            newCommands.addAll(e.getCommands()
                    .stream().filter(s -> entry.getValue().stream().noneMatch(s::contains))
                    .collect(Collectors.toList()));
        }
        if(newCommands.isEmpty()) return;
        e.getCommands().clear();
        e.getCommands().addAll(newCommands);
    }

}
