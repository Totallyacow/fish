package me.cow.fish2.listener;

import me.cow.fish2.Fishing;
import me.cow.fish2.model.RodType;
import me.cow.fish2.loot.LootTable;
import me.cow.fish2.upgrade.UpgradeTable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FishingListener implements Listener {
    private final Fishing plugin;
    private final LootTable lootTable;
    private final UpgradeTable upgradeTable;

    public FishingListener(Fishing plugin) {
        this.plugin = plugin;
        this.lootTable = plugin.getLootTable();
        this.upgradeTable = plugin.getUpgradeTable();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Player player = event.getPlayer();
        ItemStack rodItem = player.getInventory().getItemInMainHand();
        RodType rodType = RodType.fromItem(rodItem);

        if (rodType == null) {
            player.sendMessage(ChatColor.RED + "You need a special fishing rod to catch rare items!");
            return;
        }

        if (event.getHook().getLocation().getBlock().getType() != Material.WATER) {
            player.sendMessage(ChatColor.RED + "You need to fish in water to catch rare items!");
            return;
        }

        handleFishCaught(event, rodType);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FISHING_ROD && player.isSneaking()) {
            event.setCancelled(true);
            upgradeTable.openUpgradeTable(player);
        }
    }

    private void handleFishCaught(PlayerFishEvent event, RodType rodType) {
        Player player = event.getPlayer();
        ItemStack caughtItem = lootTable.rollForLoot(rodType.getLootPoolType(), plugin.getRodSlots(event.getPlayer().getInventory().getItemInMainHand()));

        if (event.getCaught() != null) {
            event.getCaught().remove();
        }

        String itemName = caughtItem.getType().name().toLowerCase().replace("_", " ");
        String message = plugin.isUpgradeGem(caughtItem)
                ? ChatColor.GREEN + "You caught an Upgrade Gem!"
                : ChatColor.GREEN + "You caught a " + itemName + "!";

        player.sendMessage(message);
        player.getInventory().addItem(caughtItem);
    }
}
