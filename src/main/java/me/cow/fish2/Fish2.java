package me.cow.fish2;

import me.cow.fish2.commands.FishingCommands;
import me.cow.fish2.commands.FishingCommandsCompleter;
import me.cow.fish2.listener.FishingListener;
import me.cow.fish2.loot.LootTable;
import me.cow.fish2.upgrade.UpgradeTable;
import me.cow.fish2.menu.UpgradableRodsMenu;
import me.cow.fish2.model.RodType;
import me.cow.fish2.util.ConfigManager;
import me.cow.fish2.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Fishing extends JavaPlugin implements Listener {

    private static final String PLAYER_MENU_ITEM_NAME = ChatColor.GOLD + "Player Menu";
    private static final int PLAYER_MENU_SLOT = 8;

    private NamespacedKey rodTypeKey;

    private ConfigManager configManager;
    private UpgradeTable upgradeTable;
    private HashMap<UUID, BitSet> unlockedSlots;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        upgradeTable = new UpgradeTable(this);
        unlockedSlots = new HashMap<>();
        rodTypeKey = new NamespacedKey(this, "rod_type");

        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("fishingcommand").setExecutor(new FishingCommands(this));
        getCommand("fishingcommand").setTabCompleter(new FishingCommandsCompleter());

        loadPlayerData();
        getLogger().info("FishingOverhaul has been enabled!");
    }

    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("FishingOverhaul has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveStaticItem(player);
        if (!hasStarterRod(player)) {
            player.getInventory().addItem(RodType.BASIC.createRodItem());
        }
    }

    private void giveStaticItem(Player player) {
        ItemStack staticItem = new ItemBuilder(Material.NETHER_STAR)
                .name(PLAYER_MENU_ITEM_NAME)
                .build();
        player.getInventory().setItem(PLAYER_MENU_SLOT, staticItem);
    }

    private boolean hasStarterRod(Player player) {
        return player.getInventory().contains(RodType.BASIC.createRodItem());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.NETHER_STAR &&
                item.getItemMeta() != null &&
                ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Player Menu")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FISHING_ROD && item.getItemMeta() != null) {
            if (event.getAction().toString().contains("RIGHT_CLICK") && player.isSneaking()) {
                new UpgradableRodsMenu(this).open(player);
                event.setCancelled(true);
            }
        }
    }

    public void openRodUpgradeMenu(Player player, RodType rodType) {
        new RodUpgradeMenu(this, player, rodType).open();
    }

    public void openEnchantmentMenu(Player player, int slotIndex) {
        new EnchantmentMenu(this, player, slotIndex).open();
    }

    public BitSet getUnlockedSlots(UUID playerUUID) {
        return unlockedSlots.computeIfAbsent(playerUUID, k -> new BitSet());
    }

    public void unlockSlot(UUID playerUUID, int slotIndex) {
        BitSet playerSlots = getUnlockedSlots(playerUUID);
        playerSlots.set(slotIndex);
    }

    public boolean isSlotUnlocked(UUID playerUUID, int slotIndex) {
        return getUnlockedSlots(playerUUID).get(slotIndex);
    }

    public int calculateGemCost(int slotsUnlocked) {
        return configManager.getGemCost(slotsUnlocked);
    }

    public boolean hasEnoughGems(Player player, int cost) {
        // Implement your currency system here
        return true; // Placeholder
    }

    private void loadPlayerData() {
        // Implement loading player data from a file or database
    }

    private void savePlayerData() {
        // Implement saving player data to a file or database
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public UpgradeTable getUpgradeTable() {
        return upgradeTable;
    }

    public ItemStack getFishingRodFromInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (RodType.isUpgradableRod(item)) {
                return item;
            }
        }
        return null;
    }

    public ItemStack getUpgradeGems(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isUpgradeGem(item)) {
                return item;
            }
        }
        return null;
    }

    public boolean isUpgradeGem(ItemStack item) {
        // Implement logic to check if the item is an upgrade gem
        return item != null && item.getType() == Material.EMERALD && item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().contains("Upgrade Gem");
    }

    public int getRodSlots(ItemStack rod) {
        if (rod != null && rod.hasItemMeta() && rod.getItemMeta().hasLore()) {
            List<String> lore = rod.getItemMeta().getLore();
            for (String line : lore) {
                if (line.startsWith(ChatColor.AQUA + "Unlocked Slots:")) {
                    return Integer.parseInt(line.split(": ")[1]);
                }
            }
        }
        return 0;
    }

    public void setRodSlots(ItemStack rod, int slots) {
        if (rod != null && rod.hasItemMeta()) {
            ItemMeta meta = rod.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            // Update or add the slots information in the lore
            boolean slotInfoUpdated = false;
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).contains("Slots:")) {
                    lore.set(i, "§7Slots: §f" + slots);
                    slotInfoUpdated = true;
                    break;
                }
            }

            if (!slotInfoUpdated) {
                lore.add("§7Slots: §f" + slots);
            }

            meta.setLore(lore);
            rod.setItemMeta(meta);
        }
    }
    public class Fish extends JavaPlugin {
        private LootTable lootTable;
        private UpgradeTable upgradeTable;

        // ... other code ...

        public LootTable getLootTable() {
            return lootTable;
        }

        public UpgradeTable getUpgradeTable() {
            return upgradeTable;
        }
        public NamespacedKey getRodTypeKey() {
            return rodTypeKey;
        }

        // ... other me

    }
}