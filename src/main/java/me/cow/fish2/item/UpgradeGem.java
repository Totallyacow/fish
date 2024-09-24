package me.cow.fish2.item;

import me.cow.fish2.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UpgradeGem {
    BASIC("Fishing Rod Upgrade Gem", Material.DIAMOND, ChatColor.LIGHT_PURPLE),
    SPECIAL("Special Fishing Rod Upgrade Gem", Material.NETHER_STAR, ChatColor.AQUA);

    private final String name;
    private final Material material;
    private final ChatColor color;

    UpgradeGem(String name, Material material, ChatColor color) {
        this.name = name;
        this.material = material;
        this.color = color;
    }

    public ItemStack createItem() {
        return new ItemBuilder(material)
                .name(color + name)
                .lore(
                        ChatColor.GRAY + "Use this gem to upgrade your fishing rods!",
                        ChatColor.GRAY + "Unlock additional slots and enhance performance."
                )
                .build();
    }

    public static boolean isUpgradeGem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        for (UpgradeGem gem : values()) {
            if (gem.name.equals(itemName) && gem.material == item.getType()) {
                return true;
            }
        }
        return false;
    }

    public static UpgradeGem getGemType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        for (UpgradeGem gem : values()) {
            if (gem.name.equals(itemName) && gem.material == item.getType()) {
                return gem;
            }
        }
        return null;
    }
}
