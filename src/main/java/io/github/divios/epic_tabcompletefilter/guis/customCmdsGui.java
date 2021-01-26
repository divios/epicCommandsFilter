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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class customCmdsGui implements InventoryHolder, Listener {

    private static final EpicCommandsFilter main = EpicCommandsFilter.getInstance();
    private static final databaseManager dbManager = databaseManager.getInstance();
    private final ArrayList<Inventory> invs = new ArrayList<>();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    private final ArrayList<String> customCmds;
    private Player p;

    public customCmdsGui(Player p, int page) {
        this.p = p;
        customCmds = dbManager.getAddedCommands();
        init();
        p.openInventory(invs.get(page));
    }

    public customCmdsGui(Player p) {
        this.p = p;
        customCmds = dbManager.getAddedCommands();
        init();
        p.openInventory(invs.get(0));
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, main);

        utils.setDisplayName(previous, "&6&lPrevious");
        utils.setDisplayName(next, "&6&lNext");

        double nD = customCmds.size() / 45F;
        int n = (int) Math.ceil(nD);

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                invs.add(createGUI(i + 1, 2));
            } else if (i == 0) invs.add(createGUI(i + 1, 0));
            else invs.add(createGUI(i + 1, 1));
        }

        if (invs.isEmpty()) {
            Inventory firstInv = Bukkit.createInventory(this, 54, utils.formatString("&b&lManage custom Cmds"));
            firstInv.setContents(getInventory().getContents());
            invs.add(firstInv);
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, utils.formatString("&&b&lManage custom Cmds"));

        ItemStack backItem = XMaterial.OAK_SIGN.parseItem();   //back button
        utils.setDisplayName(backItem, "&c&lReturn");
        utils.setLore(backItem, Arrays.asList("&7Click to go back"));

        ItemStack addCmd = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addCmd, "&6&lAdd cmd");
        utils.setLore(addCmd, Arrays.asList("&7Click to add a new command"));

        inv.setItem(51, addCmd);
        inv.setItem(49, backItem);
        return inv;
    }

    public Inventory processNextGui(Inventory inv, int dir) {
        return invs.get(invs.indexOf(inv) + dir);
    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(this, 54, utils.formatString("&b&lManage custom Cmds")
                + page);

        returnGui.setContents(getInventory().getContents());
        if (pos == 0 && customCmds.size() > 44) returnGui.setItem(53, next);
        if (pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if (pos == 2 && customCmds.size() > 44) {
            returnGui.setItem(45, previous);
        }

        for (String s : customCmds) {
            ItemStack item = XMaterial.BOOK.parseItem();
            utils.setDisplayName(item, "&f&l" + s);
            utils.setLore(item, Arrays.asList("&bClick &7to remove"));

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

        if(e.getSlot() == 51) { //boton de añadir
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

        if(e.getCurrentItem().getType() == XMaterial.BOOK.parseMaterial()) {

            new confirmIH(p, (player, aBoolean) -> {
                if(aBoolean) {
                    dbManager.getAddedCommands().remove( utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName()));
                }
                new customCmdsGui(p, invs.indexOf(e.getView().getTopInventory()));
            }, "Confirm");
        }

    }

    @EventHandler
    public void onDragEvent(InventoryDragEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }


}
