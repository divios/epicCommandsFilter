package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.databaseUtils.databaseManager;
import io.github.divios.epic_tabcompletefilter.utils;
import io.github.divios.epic_tabcompletefilter.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class commandsGui implements Listener, InventoryHolder {

    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static databaseManager dbManager = databaseManager.getInstance();
    private Player p;
    private String groupId;
    private int page = 0;
    private final ArrayList<Inventory> invs = new ArrayList<>();
    private final List<String> commandList;
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    private final ItemStack addAll = new ItemStack(Material.REDSTONE_BLOCK);
    private final ItemStack removeAll = XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem();
    private boolean isSearch = false;

    public commandsGui(Player p, String groupId, int page) {
        this.p = p;
        this.groupId = groupId;
        this.page = page;
        this.commandList = utils.getKnownCommands();
        init();
        p.openInventory(invs.get(page));
    }

    public commandsGui(Player p, String groupId) {
        this.p = p;
        this.groupId = groupId;
        this.commandList = utils.getKnownCommands();
        init();
        p.openInventory(invs.get(0));
    }

    public commandsGui(Player p, String groupId, int page, boolean isSearch, List<String> commands){
        this.p = p;
        this.groupId = groupId;
        this.isSearch = isSearch;
        commandList = commands;
        init();
        p.openInventory(invs.get(page));
    }

    public commandsGui(Player p, String groupId, boolean isSearch, List<String> commands){
        this.p = p;
        this.groupId = groupId;
        this.isSearch = isSearch;
        commandList = commands;
        init();
        p.openInventory(invs.get(0));
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, main);

        utils.setDisplayName(previous, "&6&lPrevious");
        utils.setDisplayName(next, "&6&lNext");
        utils.setDisplayName(addAll, "&c&lAdd all filters");
        utils.setDisplayName(removeAll, "&a&lRemove all filters");

        double nD = commandList.size() / 45F;
        int n = (int) Math.ceil(nD);

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                invs.add(createGUI(i + 1, 2));
            } else if (i == 0) invs.add(createGUI(i + 1, 0));
            else invs.add(createGUI(i + 1, 1));
        }

        if (invs.isEmpty()) {
            Inventory firstInv = Bukkit.createInventory(this, 54, utils.formatString("&b&lManage Group ") + groupId);
            firstInv.setContents(getInventory().getContents());
            invs.add(firstInv);
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, utils.formatString("&b&lManage Group ") + groupId);

        ItemStack backItem = XMaterial.OAK_SIGN.parseItem();   //back button
        utils.setDisplayName(backItem, "&c&lReturn");
        utils.setLore(backItem, Arrays.asList("&7Click to go back"));

        ItemStack search = null;
        if(!isSearch) {
            search = XMaterial.COMPASS.parseItem();
            utils.setDisplayName(search, "&b&lSearch");
            utils.setLore(search, Arrays.asList("&7Click to search item"));
        } else {
            search = XMaterial.REDSTONE_BLOCK.parseItem();
            utils.setDisplayName(search, "&c&lCancel search");
            utils.setLore(search, Arrays.asList("&7Click to cancel searcg"));
        }

        inv.setItem(51, search);

        inv.setItem(49, backItem);
        inv.setItem(46, removeAll);
        inv.setItem(47, addAll);
        return inv;
    }

    public Inventory processNextGui(Inventory inv, int dir) {
        return invs.get(invs.indexOf(inv) + dir);
    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(this, 54, utils.formatString("&b&lManage Group ")
                + groupId + " " + page);

        returnGui.setContents(getInventory().getContents());
        if (pos == 0 && commandList.size() > 44) returnGui.setItem(53, next);
        if (pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if (pos == 2 && commandList.size() > 44) {
            returnGui.setItem(45, previous);
        }

        HashMap<String, List<String>> filters = databaseManager.getInstance().getFilters();
        for (String s : commandList) {
            ItemStack item;
            if(filters.get(groupId).contains(s)) item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
            else item = XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseItem();
            utils.setDisplayName(item, "&f&l" + s);

            if (slot == 45 * page) break;
            if (slot >= (page - 1) * 45) returnGui.setItem(slot - (page - 1) * 45, item);

            slot++;
        }
        return returnGui;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot()) return;

        if(e.getCurrentItem() == null || utils.isEmpty(e.getCurrentItem())) return;

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 49) { //boton de salir
            p.openInventory(guiManager.getInstance().getGroupsGui());
            return;
        }

        if ( e.getSlot() == 53) { //boton de atras
            p.openInventory(processNextGui(e.getView().getTopInventory(), 1));
            return;
        }

        if ( e.getSlot() == 45) { //boton de siguiente
            p.openInventory(processNextGui(e.getView().getTopInventory(), -1));
            return;
        }

        if(e.getSlot() == 46) { //Boton de removeAll
            dbManager.getFilters().get(groupId).clear();
            new commandsGui(p, groupId);
        }

        if(e.getSlot() == 47) {
            dbManager.getFilters().get(groupId).clear();
            dbManager.getFilters().get(groupId).addAll(commandList);
            new commandsGui(p, groupId);
        }

        if(e.getSlot() == 51) {
            if(e.getCurrentItem().getType() == XMaterial.REDSTONE_BLOCK.parseMaterial()) {
                new commandsGui(p, groupId);
            } else {
                final List<String> lists = new ArrayList<>();
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            utils.runTaskLater(() -> {
                                new commandsGui(player, groupId, true, lists);
                            }, 1L);
                        })
                        .onComplete((player, text) -> {

                            List<String> lists2 = new ArrayList<>();
                            for(String s: dbManager.getFilters().get(groupId)) {
                                if(s.toLowerCase().startsWith(text.toLowerCase())) {
                                    lists2.add(s);
                                }
                            }
                            lists.addAll(lists2);
                            return AnvilGUI.Response.close();

                        })
                        .text("Insert text to search")
                        .itemLeft(new ItemStack(XMaterial.COMPASS.parseMaterial()))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Insert text to search")
                        .plugin(main)
                        .open(p);
            }
        }

        ItemStack item = e.getCurrentItem();
        String str = utils.trimString(item.getItemMeta().getDisplayName());

        if(item.getType() == XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial()) {
            dbManager.getFilters().get(groupId).add(str);
            item.setType(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial());

        }
        else if(item.getType() == XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()) {
            dbManager.getFilters().get(groupId).remove(str);
            item.setType(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial());
        }
    }

    @EventHandler
    public void onDragEvent(InventoryDragEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

}
