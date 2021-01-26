package io.github.divios.epic_tabcompletefilter;

import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import io.github.divios.epic_tabcompletefilter.guis.guiManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EpicCommandsFilter extends JavaPlugin {

    private static EpicCommandsFilter main = null;

    @Override
    public void onEnable() {
        main = this;

            //Initiate managers
        databaseManager.getInstance();
        guiManager.getInstance();

            //register Listeners
        listener.getInstance();

        commands.registerCommands();

    }

    @Override
    public void onDisable() {
        databaseManager.getInstance().saveAllFilters();
    }

    public static EpicCommandsFilter getInstance() { return main; }

}
