package org.ajls.tractorcompass;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.UUID;

import static org.ajls.tractorcompass.ItemStackModify.setClassItem;
import static org.ajls.tractorcompass.ItemStackModify.setLore;
import static org.ajls.tractorcompass.MyListener.*;
import static org.ajls.tractorcompass.ScoreboardsAndTeams.*;

public class CompassItemStack {
    public static ItemStack tractorCompass;
    public static void createCompass() {
        ItemStack itemStack = new ItemStack(Material.COMPASS);
        itemStack = setLore(itemStack, "tractor_compass");
        setClassItem(itemStack);
        CompassMeta itemMeta = (CompassMeta) itemStack.getItemMeta();
//        itemMeta.setLodestoneTracked(true);
        itemStack.setItemMeta(itemMeta);
        tractorCompass = itemStack;
    }
    public static void givePlayerCompass(Player player, int slot) {
//        ItemStack tractorCompass = new ItemStack(Material.COMPASS);
//        tractorCompass = setLore(tractorCompass, "tractor_compass");
        player.getInventory().setItem(slot, tractorCompass);
    }

    public static boolean checkEqualsCompass(ItemStack itemStack) {
        if (itemStack.getItemMeta() != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            ItemMeta tractorMeta = tractorCompass.getItemMeta();
            if (itemMeta.getLore() != null) {
                if (itemMeta.getLore().equals(tractorMeta.getLore())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void trackTeam(Player player) {
        UUID playerUUID  = player.getUniqueId();
        if (!tracker_team.containsKey(playerUUID)) {
            tracker_team.put(playerUUID, 0);
        }
        String trackerTeamName = getPlayerTeamName(player);
        String teamName = tracked_teams.get(tracker_team.get(player.getUniqueId()));
        while (!trackerTeamName.equals(teamName) && not_tracked_teams.contains(teamName)) {  // not same team and wither dead
            int team = tracker_team.get(player.getUniqueId()) + 1 ;
            tracker_team.put(player.getUniqueId(), tracker_team.get(player.getUniqueId()) + 1);
            if (team >= tracked_teams.size()) {
                tracker_team.put(player.getUniqueId(), 0);
            }
            teamName = tracked_teams.get(tracker_team.get(player.getUniqueId()));
        }

//        player.sendMessage(ChatColor.GREEN + "tracking " + getTeamColor(teamName) + teamName);
        World world = player.getWorld();
        Location loc = player.getLocation();
        double distance = Double.MAX_VALUE;
        Player target = null;
        Location targetLoc = null;
        for (Player p : world.getPlayers()) {
            if (!p.equals(player) && p.getGameMode() != GameMode.SPECTATOR) {
                if (getPlayerTeam(p).getName().equals(teamName)) {
                    Location loc2 = p.getLocation();
                    double distance2 = loc2.distance(loc);
                    if (distance2 < distance) {
                        distance = distance2;
                        target = p;
                        targetLoc = loc2;
                    }
                }
            }
        }
        if (target != null) {
            player.setCompassTarget(targetLoc);
            ChatColor chatColor = getPlayerTeam(target).getColor();
            boolean up = targetLoc.getY() > loc.getY();
            if (up) {
//                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(chatColor + "" + Math.round(distance) + "m " + ChatColor.WHITE + "上方"));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(chatColor + "" + Math.round(distance) + "m " + ChatColor.WHITE + "上方"));
            }
            else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(chatColor + "" + Math.round(distance) + "m " + ChatColor.WHITE + "下方"));
            }
        }
        else {
            player.setCompassTarget(new Location(world, 0, 0, 0));
            ChatColor chatColor = getTeamColor(teamName);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(chatColor + "此队伍无人"));
        }

    }
}
