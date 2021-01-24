package io.github.divios.epic_tabcompletefilter.guis;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.utils;
import io.github.divios.epic_tabcompletefilter.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class confirmIH implements InventoryHolder, Listener {

    private final Player p;
    private final BiConsumer<Player, Boolean> bi;
    private Consumer<Player> c;
    private final String title;
    private static final EpicCommandsFilter main = EpicCommandsFilter.getInstance();

    /**
     * @param p          Player to show the GUI
     * @param true_false Block of code to execute
     * @param title      Title of the GUI
     */

    public confirmIH(Player p,
                     BiConsumer<Player, Boolean> true_false,
                     String title) {

        Bukkit.getPluginManager().registerEvents(this, main);
        this.p = p;
        bi = true_false;
        this.title = title;
        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, title);

        ItemStack true_ = XMaterial.EMERALD_BLOCK.parseItem();
        utils.setDisplayName(true_, "&a&lConfirm");

        ItemStack false_ = XMaterial.REDSTONE_BLOCK.parseItem();
        utils.setDisplayName(false_, "&c&lCancel");

        inv.setItem(15, false_);
        inv.setItem(11, true_);

        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this)

            return;

        e.setCancelled(true);

        if (e.getInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        if (e.getInventory().getHolder() != this)
            return;

        if (e.getSlot() != e.getRawSlot()) return;

        switch (e.getSlot()) {
            case 15:
                bi.accept(p, false);
                break;

            case 11:
                bi.accept(p, true);
                break;

            default:
                break;
        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getView().getTopInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}