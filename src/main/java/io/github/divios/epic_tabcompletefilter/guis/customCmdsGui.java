package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.builders.dynamicGui;
import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import io.github.divios.epic_tabcompletefilter.utils;
import io.github.divios.epic_tabcompletefilter.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class customCmdsGui{

    private static final EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static final databaseManager dbManager = databaseManager.getInstance();
    private static final guiManager GuiManager = guiManager.getInstance();
    private final ArrayList<String> customCmds;
    private final Player p;


    public customCmdsGui(Player p) {
        this.p = p;
        this.customCmds = dbManager.getAddedCommands();
        new dynamicGui.Builder()
                .contents(this::createItemContents)
                .back(GuiManager::openGroupsGui)
                .addItems((inventory, integer) -> setAllItems(inventory))
                .contentAction(this::contentAction)
                .nonContentAction(this::nonContentAction)
                .title(integer -> "&6&lManage CustomCmds " + integer)
                .open(p);
    }

    private List<ItemStack> createItemContents() {
        List<ItemStack> contents = new ArrayList<>();
        for(String s: customCmds) {
            ItemStack item = new ItemStack(Material.BOOK);
            utils.setDisplayName(item, "&f&l" + s);
            contents.add(item);
        }
        return contents;
    }

    private void setAllItems(Inventory inv) {
        ItemStack addCmd = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addCmd, "&6&lAdd cmd");
        utils.setLore(addCmd, Arrays.asList("&7Click to add a new command"));
        inv.setItem(51, addCmd);
    }

    private dynamicGui.Response contentAction(ItemStack item) {
        if(item.getType() != Material.BOOK) return dynamicGui.Response.nu();

        new confirmIH(p, (player, aBoolean) -> {
            if (aBoolean) {
                String cmd = utils.trimString(item.getItemMeta().getDisplayName());
                dbManager.getAddedCommands().remove(cmd);
            }
        }, "&aConfirm");
        return dynamicGui.Response.update();
    }

    private dynamicGui.Response nonContentAction(int slot) {
        if(slot == 51) {
            new AnvilGUI.Builder()
                    .onClose(player -> {                                        //called when the inventory is closing
                        utils.runTaskLater(() -> new customCmdsGui(player), 1L);
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        if (text.isEmpty()) {
                            return AnvilGUI.Response.text("Incorrect.");
                        }
                        dbManager.getAddedCommands().add(text.toLowerCase(Locale.ROOT));
                        return AnvilGUI.Response.close();
                    })
                    .text("Enter cmd name")                       //sets the text the GUI should start with
                    .itemLeft(XMaterial.COMMAND_BLOCK.parseItem())               //use a custom item for the first slot//use a custom item for the second slot
                    .title("Enter cmd name")                                //set the title of the GUI (only works in 1.14+)
                    .plugin(main)                                   //set the plugin instance
                    .open(p);
        }
        return dynamicGui.Response.nu();
    }

}
