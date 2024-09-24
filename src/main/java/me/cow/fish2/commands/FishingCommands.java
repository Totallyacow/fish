package me.cow.fish2.commands;

import me.cow.fish2.Fishing;
import me.cow.fish2.model.RodType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FishingCommands implements CommandExecutor {

    private final Fishing plugin;

    public FishingCommands(Fishing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /fishingcommand <rodType>");
            return true;
        }

        String rodTypeName = args[0].toUpperCase();
        RodType rodType;
        try {
            rodType = RodType.valueOf(rodTypeName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Unknown rod type: " + rodTypeName);
            return true;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full! Please make space.");
            return true;
        }

        ItemStack rodItem = rodType.createItem(0); // Start with 0 unlocked slots
        player.getInventory().addItem(rodItem);
        player.sendMessage(ChatColor.GREEN + "You have been given a " + rodType.getDisplayName() + "!");
        return true;
    }
}
