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
        utils.setDisplayName(newGroup, "&6&lAdd new Group");
        utils.setLore(newGroup, Arrays.asList("&7Click to add a new group"));

        ItemStack exit = new ItemStack(Material.OAK_SIGN);
        utils.setDisplayName(exit, "&c&lExit");

        ItemStack reloadCommands = XMaterial.CRAFTING_TABLE.parseItem();
        utils.setDisplayName(reloadCommands, "&6&lReload Commands");
        utils.setLore(reloadCommands, Arrays.asList("&7Click reload and apply",
                "&7all changes to players"));

        ItemStack aux = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
        int slot = 0;
        for(Map.Entry<String, List<String>> entry: dbmanager.getFilters().entrySet()) {
            ItemStack item = aux.clone();
            utils.setDisplayName(item, "&b&l" + entry.getKey());
            utils.setLore(item, Arrays.asList("&7Click to manage the", "&7group commands"));
            inv.setItem(slot, item);
            slot++;
        }

        inv.setItem(47, reloadCommands);
        inv.setItem(49, exit);
        inv.setItem(51, newGroup);
        return inv;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot() || utils.isEmpty(e.getCurrentItem())) return;
        Player p = (Player) e.getWhoClicked();

        if(e.getSlot() == 51) {
            new AnvilGUI.Builder()
                    .onClose(player -> {                                        //called when the inventory is closing
                        Bukkit.getScheduler().runTaskLater(main, () -> p.openInventory(getInventory()), 1);
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

        if(e.getSlot() == 49) { p.closeInventory(); return; }

        if(e.getSlot() == 47) {
            utils.updateAllPlayersCommands();
            p.sendMessage(utils.formatString("&b&lEpicCommandsFilter > &7Reloaded all commands"));
            return; }

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
