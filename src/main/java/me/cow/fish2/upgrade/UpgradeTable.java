package me.cow.fish2.upgrade;

import me.cow.fish2.Fishing;
import me.cow.fish2.model.RodType;
import me.cow.fish2.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradeTable implements Listener {

    private final Fishing plugin;
    private static final String INVENTORY_TITLE = ChatColor.BLUE + "Upgrade Table";
    private static final int ROD_SLOT = 11;
    private static final int GEMS_SLOT = 15;
    private static final int UPGRADE_BUTTON_SLOT = 13;

    public UpgradeTable(Fishing plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openUpgradeTable(Player player) {
        ItemStack rod = plugin.getFishingRodFromInventory(player);
        if (rod == null || !RodType.isUpgradableRod(rod)) {
            player.sendMessage(ChatColor.RED + "You need to hold a valid fishing rod to use the upgrade table!");
            return;
        }

        ItemStack gems = plugin.getUpgradeGems(player);
        if (gems == null || gems.getAmount() == 0) {
            player.sendMessage(ChatColor.RED + "You need at least one upgrade gem to use the upgrade table!");
            return;
        }

        Inventory upgradeInventory = Bukkit.createInventory(null, 27, INVENTORY_TITLE);
        upgradeInventory.setItem(ROD_SLOT, rod);
        upgradeInventory.setItem(GEMS_SLOT, gems);
        upgradeInventory.setItem(UPGRADE_BUTTON_SLOT, createUpgradeButton());

        player.openInventory(upgradeInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_TITLE)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (event.getSlot() == ROD_SLOT || event.getSlot() == GEMS_SLOT) {
            event.setCancelled(false);
            return;
        }

        if (event.getSlot() == UPGRADE_BUTTON_SLOT) {
            upgradeRod(player, inventory.getItem(ROD_SLOT), inventory.getItem(GEMS_SLOT));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_TITLE)) return;

        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        returnItemToPlayer(player, inventory.getItem(ROD_SLOT));
        returnItemToPlayer(player, inventory.getItem(GEMS_SLOT));
    }

    private void upgradeRod(Player player, ItemStack rod, ItemStack gems) {
        if (rod == null || gems == null) return;

        RodType rodType = RodType.fromItem(rod);
        if (rodType == null) return;

        int currentSlots = plugin.getRodSlots(rod);
        if (currentSlots >= rodType.getMaxSlots()) {
            player.sendMessage(ChatColor.RED + "Your fishing rod already has the maximum number of slots.");
            return;
        }

        if (!plugin.isUpgradeGem(gems)) {
            player.sendMessage(ChatColor.RED + "You need a valid upgrade gem to upgrade your rod!");
            return;
        }

        plugin.setRodSlots(rod, currentSlots + 1);
        gems.setAmount(gems.getAmount() - 1);
        player.sendMessage(ChatColor.GREEN + "Your fishing rod has been upgraded! It now has " + (currentSlots + 1) + " slots.");
    }

    private void returnItemToPlayer(Player player, ItemStack item) {
        if (item != null) {
            player.getInventory().addItem(item).values().forEach(leftover ->
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        }
    }

    private ItemStack createUpgradeButton() {
        return new ItemBuilder(Material.ANVIL)
                .name(ChatColor.GREEN + "Upgrade Rod")
                .lore(ChatColor.GRAY + "Click to upgrade your fishing rod!")
                .build();
    }
}
