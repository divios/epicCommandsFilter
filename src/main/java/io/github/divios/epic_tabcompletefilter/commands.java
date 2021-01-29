package io.github.divios.epic_tabcompletefilter;

import io.github.divios.epic_tabcompletefilter.guis.guiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class commands implements CommandExecutor {

    private static commands instance = null;
    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static guiManager Guimanager = null;

    public static void registerCommands() {
        instance = new commands();
        Guimanager = guiManager.getInstance();
        main.getCommand("epicCommandsFilter").setExecutor(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;

        if(!sender.hasPermission("ecf.open")) {
            sender.sendMessage(utils.formatString("&b&lEpicCommandsFilter > &7You dont have permissions"));
            return true;
        }

        Player p = (Player) sender;
        Guimanager.openGroupsGui(p);

        return true;
    }
}
