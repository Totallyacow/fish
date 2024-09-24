package me.cow.fish2.loot;

import me.cow.fish2.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    private final Map<String, List<LootItem>> lootPools;
    private final Random random;

    public LootTable(ConfigurationSection config) {
        this.lootPools = new EnumMap<>(LootPoolType.class);
        this.random = new Random();
        initializeLootPools(config);
    }

    private void initializeLootPools(ConfigurationSection config) {
        for (LootPoolType poolType : LootPoolType.values()) {
            lootPools.put(poolType.name().toLowerCase(), createLootPool(config.getConfigurationSection(poolType.name().toLowerCase())));
        }
    }

    private List<LootItem> createLootPool(ConfigurationSection poolConfig) {
        List<LootItem> pool = new ArrayList<>();
        for (String key : poolConfig.getKeys(false)) {
            ConfigurationSection itemConfig = poolConfig.getConfigurationSection(key);
            if (itemConfig != null) {
                pool.add(createLootItem(itemConfig));
            }
        }
        return pool;
    }

    private LootItem createLootItem(ConfigurationSection itemConfig) {
        Material material = Material.valueOf(itemConfig.getString("material"));
        int chance = itemConfig.getInt("chance");
        int minAmount = itemConfig.getInt("min_amount", 1);
        int maxAmount = itemConfig.getInt("max_amount", 1);

        if (material == Material.EMERALD && itemConfig.contains("gem_level")) {
            String displayName = itemConfig.getString("display_name");
            int gemLevel = itemConfig.getInt("gem_level");
            return new LootItem(createGemItem(displayName, gemLevel), chance, minAmount, maxAmount);
        }

        return new LootItem(material, chance, minAmount, maxAmount);
    }

    public ItemStack rollForLoot(LootPoolType poolType, int luckBoost) {
        List<LootItem> pool = lootPools.get(poolType.name().toLowerCase());
        if (pool == null || pool.isEmpty()) {
            return new ItemStack(Material.COD);
        }

        int roll = Math.min(random.nextInt(100) + luckBoost, 100);
        int cumulativeChance = 0;

        for (LootItem item : pool) {
            cumulativeChance += item.getChance();
            if (roll <= cumulativeChance) {
                return item.createItemStack();
            }
        }

        return new ItemStack(Material.COD);
    }

    private ItemStack createGemItem(String displayName, int gemLevel) {
        return new ItemBuilder(Material.EMERALD)
                .name(displayName)
                .lore(
                        ChatColor.GRAY + "Gem Level: " + gemLevel,
                        ChatColor.GREEN + "Use this to unlock a slot on your fishing rod."
                )
                .build();
    }

    public enum LootPoolType {
        BASIC, ADVANCED, RARE
    }
}

class LootItem {
    private final ItemStack itemStack;
    private final int chance;
    private final int minAmount;
    private final int maxAmount;

    public LootItem(Material material, int chance, int minAmount, int maxAmount) {
        this(new ItemStack(material), chance, minAmount, maxAmount);
    }

    public LootItem(ItemStack itemStack, int chance, int minAmount, int maxAmount) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public int getChance() {
        return chance;
    }

    public ItemStack createItemStack() {
        ItemStack clonedItem = itemStack.clone();
        clonedItem.setAmount(minAmount + (int) (Math.random() * (maxAmount - minAmount + 1)));
        return clonedItem;
    }
}
