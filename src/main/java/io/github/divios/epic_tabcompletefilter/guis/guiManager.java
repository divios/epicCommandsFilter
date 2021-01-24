package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

    public Inventory getGroupsGui() {
            return GroupsGui.getInventory();
    }

    public void openCommandsGui(Player p, String grouid) {
        new commandsGui(p, grouid);
    }


}
