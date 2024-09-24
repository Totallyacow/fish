package me.cow.fish2.commands;

import me.cow.fish2.model.RodType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FishingCommandsCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String partialRodType = args[0].toUpperCase();
            return Arrays.stream(RodType.values())
                    .map(Enum::name)
                    .filter(name -> name.startsWith(partialRodType))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
