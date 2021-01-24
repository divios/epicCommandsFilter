package io.github.divios.epic_tabcompletefilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class utils {

    private static final EpicCommandsFilter main = EpicCommandsFilter.getInstance();

    public static void setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(formatString(name));
        item.setItemMeta(meta);
    }

    public static void setLore(ItemStack item, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        List<String> coloredLore = new ArrayList<>();
        for(String s: lore) {
            coloredLore.add(formatString(s));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
    }

    public static String formatString(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String trimString(String str) {
        return ChatColor.stripColor(str);
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Queue a task to be run asynchronously. <br>
     *
     * @param runnable task to run
     */
    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(main, runnable);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(main, runnable);
    }

    public static List<String> getKnownCommands() {
        SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();

        try {
            Field commandMap = SimplePluginManager.class.getDeclaredField("commandMap");
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");

            commandMap.setAccessible(true);
            knownCommands.setAccessible(true);

            return new ArrayList<> ( ((Map<String, Command>) knownCommands.get(commandMap.get(spm))).values())
                    .stream().map(command -> command.getName().toLowerCase(Locale.ROOT)).distinct().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());


        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void updateAllPlayersCommands() {
        for(Player p: main.getServer().getOnlinePlayers()) {
            p.updateCommands();
        }
    }

}
