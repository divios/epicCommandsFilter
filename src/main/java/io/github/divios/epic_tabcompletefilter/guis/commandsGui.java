package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.builders.dynamicGui;
import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import io.github.divios.epic_tabcompletefilter.utils;
import io.github.divios.epic_tabcompletefilter.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class commandsGui {

    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static databaseManager dbManager = databaseManager.getInstance();
    private static guiManager GuiManager = guiManager.getInstance();
    private Player p;
    private String groupId;
    private final List<String> commandList;
    private final List<String> filters;

    public commandsGui(Player p, String groupId) {
        this.p = p;
        this.groupId = groupId;
        this.commandList = utils.getKnownCommands();
        this.filters = databaseManager.getInstance().getFilters().get(groupId);

        new dynamicGui.Builder()
                .contents(this::createItemContents)
                .back(player -> GuiManager.openGroupsGui(player))
                .title(page -> "&b&lManage commands Page " + page)
                .contentAction(itemStack -> {
                    toggleMaterial(itemStack);
                    return dynamicGui.Response.nu();
                })
                .rows(45)
                .addItems((inventory, integer) -> setAllItems(inventory))
                .nonContentAction(integer -> {
                    if(integer == 46) {
                        dbManager.getFilters().get(groupId).clear();
                        return dynamicGui.Response.update();
                    }
                    else if (integer == 47) {
                        dbManager.getFilters().get(groupId).clear();
                        dbManager.getFilters().get(groupId).addAll(commandList);
                        return dynamicGui.Response.update();
                    }
                    return dynamicGui.Response.nu();
                })
                .open(p);
    }


    private List<ItemStack> createItemContents() {
        List<ItemStack> itemsContents = new ArrayList<>();

        for(String s : commandList) {
            ItemStack item = new ItemStack(Material.GRASS);
            utils.setDisplayName(item, "&f&l" + s);
            utils.setLore(item, Arrays.asList("&7Click to toggle"));
            if(filters.contains(s)) item.setType(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial());
            else item.setType(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial());

            itemsContents.add(item);
        }
        return itemsContents;
    }

    private void toggleMaterial(ItemStack item) {
        if (item.getType() == XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial()) {
            item.setType(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial());
        }
        else item.setType(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial());
    }

    private void setAllItems(Inventory inv) {
        ItemStack addAll = new ItemStack(Material.REDSTONE_BLOCK);
        ItemStack removeAll = XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem();
        utils.setDisplayName(addAll, "&c&lAdd all filters");
        utils.setDisplayName(removeAll, "&b&lRemove all filters");

        inv.setItem(46, removeAll);
        inv.setItem(47, addAll);
    }

}
