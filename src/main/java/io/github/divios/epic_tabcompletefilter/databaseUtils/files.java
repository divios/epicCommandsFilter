package io.github.divios.epic_tabcompletefilter.databaseUtils;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;

import java.io.File;
import java.io.IOException;

public class files {

    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();

    public static void createDatabase(){
        File localeDirectory = new File(main.getDataFolder().toString());

        if (!localeDirectory.exists() && !localeDirectory.isDirectory()) {
            localeDirectory.mkdir();
        }

        File file = new File(main.getDataFolder(), main.getDescription().getName().toLowerCase() + ".db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                main.getServer().getPluginManager().disablePlugin(main);
            }
        }
    }

}
