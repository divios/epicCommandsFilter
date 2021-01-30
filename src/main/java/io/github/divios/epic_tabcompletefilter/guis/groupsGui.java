package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import io.github.divios.epic_tabcompletefilter.utils;
import io.github.divios.epic_tabcompletefilter.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class groupsGui implements Listener, InventoryHolder {

    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static databaseManager dbmanager = databaseManager.getInstance();

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, utils.formatString("&c&lGroups manager"));

        ItemStack newGroup = new ItemStack(Material.ANVIL);
        utils.setDisplayName(newGroup, "&6&lAdd new Command");
        utils.setLore(newGroup, Arrays.asList("&7Even though the plugin does most of", "&7the dirty work for you, is",
                "&7not perfect and cannot hook to", "&7all commands.", "", "&7If you want to add a new command",
                "&7that does not appear on the default list", "&7then click here"));

        ItemStack exit = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(exit, "&c&lExit");

        ItemStack reloadCommands = XMaterial.CRAFTING_TABLE.parseItem();
        utils.setDisplayName(reloadCommands, "&6&lReload Commands");
        utils.setLore(reloadCommands, Arrays.asList("&7Click reload and apply",
                "&7all changes to players"));

        ItemStack info = XMaterial.BOOK.parseItem();
        utils.setDisplayName(info, "&b&lWhat is this?");
        utils.setLore(info, Arrays.asList("&7This menu displays all the current groups",
                "&7the plugin has. To attach a player to a group", "&7just give them the permission: &becf.{group}&7,",
                "&7being {group} the name of the group", "", "&7Note: a player can belong to several groups",
                "&7at the same time, but can cause conflicts, be aware", "","&7To add a new group just click",
                "&7on an empty slot"));

        ItemStack aux = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
        int slot = 0;
        for(Map.Entry<String, List<String>> entry: dbmanager.getFilters().entrySet()) {
            ItemStack item = aux.clone();
            utils.setDisplayName(item, "&b&l" + entry.getKey());
            utils.setLore(item, Arrays.asList("&7Click to manage the", "&7group commands"));
            inv.setItem(slot, item);
            slot++;
        }

        inv.setItem(45, info);
        inv.setItem(47, reloadCommands);
        inv.setItem(49, exit);
        inv.setItem(51, newGroup);
        return inv;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot()) return;
        Player p = (Player) e.getWhoClicked();

        if(e.getSlot() == 46 || e.getSlot() == 48 || e.getSlot() == 50 ||
                e.getSlot() == 52 || e.getSlot() == 53 || e.getSlot() == -999) return;

        if(e.getSlot() == 51) {
            guiManager.getInstance().openCustomCmdsGui(p);
            return;
        }

        if(e.getSlot() == 49) { p.closeInventory(); return; }

        if(e.getSlot() == 47) {
            utils.updateAllPlayersCommands();
            p.sendMessage(utils.formatString("&b&lEpicCommandsFilter > &7Reloaded all commands"));
            return; }

        if(utils.isEmpty(e.getCurrentItem())) {
            new AnvilGUI.Builder()
                    .onClose(player -> {                                        //called when the inventory is closing
                        utils.runTaskLater(() -> p.openInventory(getInventory()), 1L);
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        if(text.isEmpty()) {
                            return AnvilGUI.Response.text("Incorrect.");
                        }
                        dbmanager.createNewGroup(text);
                        return AnvilGUI.Response.close();
                    })
                    .text("Enter group name")                       //sets the text the GUI should start with
                    .itemLeft(new ItemStack(Material.PAINTING))               //use a custom item for the first slot//use a custom item for the second slot
                    .title("Enter group name")                                //set the title of the GUI (only works in 1.14+)
                    .plugin(main)                                   //set the plugin instance
                    .open(p);
            return;
        }

        if(e.isLeftClick()) {
            String groupstr = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());
            guiManager.getInstance().openCommandsGui(p, groupstr);
            return;
        }

        if(e.isRightClick()) {
            new confirmIH(p, (player, aBoolean) -> {
                if(aBoolean) {
                    String groupstr = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());
                    dbmanager.getFilters().remove(groupstr);
                } p.openInventory(getInventory());
            }, utils.formatString("Confirm action"));
        }
    }

    @EventHandler
    public void onDragEvent(InventoryDragEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

    }


}
