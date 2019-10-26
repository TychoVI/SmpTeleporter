package dev.tycho.SmpTravelPoints.Command;

import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetIconCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            sender.sendMessage(ChatColor.GREEN + "Please punch the teleporter with the item you want as icon in your hand. Or type this command again to cancel setting an icon.");
            SmpTravelPoints.iconSetters.add(((Player) sender).getUniqueId());
        }

        return true;
    }
}
