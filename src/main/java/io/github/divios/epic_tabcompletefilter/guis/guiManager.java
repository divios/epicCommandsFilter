package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class guiManager {

    private EpicCommandsFilter main = null;
    private groupsGui GroupsGui = null;
    private static guiManager instance = null;

    public static guiManager getInstance() {
        if(instance == null) instance = new guiManager();
        return instance;
    }

    private guiManager() {
        main = EpicCommandsFilter.getInstance();
        GroupsGui = new groupsGui();

        Bukkit.getPluginManager().registerEvents(GroupsGui, main);
    }

    public void openGroupsGui(Player p) {
            p.openInventory(GroupsGui.getInventory());
    }

    public void openCustomCmdsGui(Player p) {
        new customCmdsGui(p);
    }

    public void openCommandsGui(Player p, String grouid) {
        new commandsGui(p, grouid);
    }


}
