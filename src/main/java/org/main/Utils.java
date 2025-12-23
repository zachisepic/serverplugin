package org.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public static void msgPlayer(Player player, String... strings){
        for (String string : strings) {
            player.sendMessage(color(string));
        }
    }
}
