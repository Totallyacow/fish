package me.cow.fish2.menu;

import me.cow.fish2.Fishing;
import me.cow.fish2.model.RodType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradableRodsMenu {
    private final Fishing plugin;
    private static final String MENU_TITLE = ChatColor.GREEN + "Upgradable Rods";

    public UpgradableRodsMenu(Fishing plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        int size = plugin.getConfigManager().getInventorySize("upgradable_rods");
        Inventory menu = Bukkit.createInventory(null, size, MENU_TITLE);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && RodType.isUpgradableRod(item)) {
                menu.addItem(item);
            }
        }

        player.openInventory(menu);
    }
}