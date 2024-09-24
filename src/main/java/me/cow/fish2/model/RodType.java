package me.cow.fish2.model;

import me.cow.fish2.loot.LootTable;
import me.cow.fish2.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;

import java.util.EnumMap;
import java.util.Map;

public enum RodType {
    BASIC("Basic Fishing Rod", 3, "basic_rod"),
    ADVANCED("Advanced Fishing Rod", 5, "advanced_rod"),
    EXPERT("Expert Fishing Rod", 7, "expert_rod");

    private final String displayName;
    private final int maxSlots;
    private final String key;
    private static final Map<String, RodType> keyToType = new EnumMap<>(RodType.class);

    static {
        for (RodType type : values()) {
            keyToType.put(type.key, type);
        }
    }

    RodType(String displayName, int maxSlots, String key) {
        this.displayName = ChatColor.GREEN + displayName;
        this.maxSlots = maxSlots;
        this.key = key;
    }

    public static RodType fromKey(String key) {
        return keyToType.get(key);
    }

    public static RodType fromItem(ItemStack item, NamespacedKey namespacedKey) {
        if (item != null && item.getType() == Material.FISHING_ROD && item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            String rodKey = container.get(namespacedKey, PersistentDataType.STRING);
            return fromKey(rodKey);
        }
        return null;
    }

    public ItemStack createItem(int unlockedSlots, NamespacedKey namespacedKey) {
        return new ItemBuilder(Material.FISHING_ROD)
                .name(displayName)
                .lore(
                        ChatColor.YELLOW + "Max Slots: " + maxSlots,
                        ChatColor.AQUA + "Unlocked Slots: " + unlockedSlots,
                        ChatColor.GRAY + "Use upgrade gems to unlock more slots!"
                )
                .persistentData(namespacedKey, PersistentDataType.STRING, key)
                .build();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public String getKey() {
        return key;
    }

    public static boolean isUpgradableRod(ItemStack item, NamespacedKey namespacedKey) {
        return fromItem(item, namespacedKey) != null;
    }
    public LootTable.LootPoolType getLootPoolType() {
        switch (this) {
            case BASIC:
                return LootTable.LootPoolType.BASIC;
            case ADVANCED:
                return LootTable.LootPoolType.ADVANCED;
            case EXPERT:
                return LootTable.LootPoolType.RARE;
            default:
                return LootTable.LootPoolType.BASIC;
        }
    }
}
