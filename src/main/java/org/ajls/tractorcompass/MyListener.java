package org.ajls.tractorcompass;

import org.ajls.lib.utils.PlayerU;
import org.ajls.lib.utils.ScoreboardU;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static org.ajls.tractorcompass.CompassItemStack.*;
import static org.ajls.tractorcompass.ScoreboardsAndTeams.*;
import static org.ajls.tractorcompass.TractorCompass.plugin;

public class MyListener implements Listener {
    public static HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    public static HashMap<UUID, Integer>  tracker_team = new HashMap<>();// the team of the tractor
    public static ArrayList<String> tracked_teams = new ArrayList<>(); // the teams registered

    public static ArrayList<String> getTracked_teams() {
        return tracked_teams;
    }

    public static void setTracked_teams(ArrayList<String> teams) {
        MyListener.tracked_teams = teams;
    }
    public static ArrayList<String> not_tracked_teams = new ArrayList<>(); //the teams that temperately unable to track by due to wither dead

    public static ArrayList<String> getNot_tracked_teams() {
        return not_tracked_teams;
    }

    public static void setNot_tracked_teams(ArrayList<String> not_tracked_teams) {
        MyListener.not_tracked_teams = not_tracked_teams;
    }
    public static String just_not_tracked_team = null;

    public static void setJust_not_tracked_team(String just_not_tracked_team) { // the team that wither just died cancel compass target
        MyListener.just_not_tracked_team = just_not_tracked_team;
        World world = Bukkit.getWorld("world");
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID pUUID = p.getUniqueId();
            if (tracker_team.containsKey(pUUID)) {
                String teamName = tracked_teams.get(tracker_team.get(pUUID));
                if (teamName.equals(just_not_tracked_team)) {
                    p.setCompassTarget(new Location(world, 0, 0, 0));
                }
            }
        }
    }

    public static HashSet<UUID> isHoldingTrackerCompass = new HashSet<>();

    public static HashSet<UUID> getIsHoldingTrackerCompass() {
        return isHoldingTrackerCompass;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        World world = player.getWorld();

        if (!tracker_team.containsKey(playerUUID)) {
            tracker_team.put(playerUUID, 0);
            player.setCompassTarget(new Location(world, 0, 0, 0));
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (checkEqualsCompass(itemInHand)) {
//            isHoldingTrackerCompass.add(player.getUniqueId());
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            BukkitTask task = scheduler.runTaskTimer(plugin, () -> {
                trackTeam(player);
            }, 0, 20);
            tasks.put(player.getUniqueId(), task);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BukkitTaskUtils.removeTask(playerUUID, tasks);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getHand().name().equals("HAND")) {
            Action action = event.getAction();
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
                if (itemInHand.getItemMeta() != null) {
                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    ItemMeta tractorMeta = tractorCompass.getItemMeta();
                    if (itemMeta.getLore() != null) {
                        if (itemMeta.getLore().equals(tractorMeta.getLore())) {
                            event.setCancelled(true);
                            CompassMeta compassMeta = (CompassMeta) itemMeta;
                            World world = player.getWorld();
                            Location loc = player.getLocation();
                            double distance = Float.MAX_VALUE;
                            Player target = null;
                            Location targetLoc = null;
                            String teamName = null;
                            String tractorTeamName = getPlayerTeamName(player);
                            while (true) {
                                if (!tracker_team.containsKey(player.getUniqueId())) {
                                    tracker_team.put(player.getUniqueId(), 0);
                                }
                                else {
                                    int team = tracker_team.get(player.getUniqueId()) + 1 ;
                                    tracker_team.put(player.getUniqueId(), tracker_team.get(player.getUniqueId()) + 1);
                                    if (team >= tracked_teams.size()) {
                                        tracker_team.put(player.getUniqueId(), 0);
                                    }
                                }     // select team to track
                                teamName = tracked_teams.get(tracker_team.get(player.getUniqueId()));
                                if (!continueLoop(player)) {break;}
//                                if (tractorTeamName.equals(teamName) || !not_tracked_teams.contains(teamName)) { // same team or wither not dead
//                                    break;
//                                }
                            }

                            player.sendMessage(ChatColor.GREEN + "tracking " + getTeamColor(teamName) + teamName);
                            for (Player p : world.getPlayers()) {
                                if (!p.equals(player) && p.getGameMode() != GameMode.SPECTATOR) {
                                    if (ScoreboardU.getPlayerTeam(p) != null) {
                                        if (ScoreboardU.getPlayerTeamName(p).equals(teamName)) {
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
                            }
                            if (target != null) {
                                player.setCompassTarget(targetLoc);
//                                compassMeta.setLodestone(targetLoc);
//                                itemInHand.setItemMeta(compassMeta);
//                                player.getInventory().setItemInMainHand(itemInHand);
                            }
                            else {
                                player.sendMessage(getTeamColor(teamName) + teamName + ChatColor.RED + " not found");
                            }
                        }
                    }
                }
            }
            else if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (itemInHand.getItemMeta() != null) {
                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    ItemMeta tractorMeta = tractorCompass.getItemMeta();
                    if (itemMeta.getLore() != null) {
                        if (itemMeta.getLore().equals(tractorMeta.getLore())) {
                            event.setCancelled(true);
                            CompassMeta compassMeta = (CompassMeta) itemMeta;
                            World world = player.getWorld();
                            Location loc = player.getLocation();
                            double distance = Float.MAX_VALUE;
                            Player target = null;
                            Location targetLoc = null;
                            tracker_team.put(playerUUID, -1); // nearest enemy
                            player.sendMessage(ChatColor.GREEN + "tracking" + ChatColor.WHITE + " nearest enemy");
                            for (Player p : world.getPlayers()) {
                                if (PlayerU.isPlayer2PlayableEnemy(player, p)) {  //!p.equals(player) && p.getGameMode() != GameMode.SPECTATOR
                                    Location loc2 = p.getLocation();
                                    double distance2 = loc2.distance(loc);
                                    if (distance2 < distance) {
                                        distance = distance2;
                                        target = p;
                                        targetLoc = loc2;
                                    }
                                }
                            }
                            if (target != null) {
                                player.setCompassTarget(targetLoc);
//                                compassMeta.setLodestone(targetLoc);
//                                itemInHand.setItemMeta(compassMeta);
//                                player.getInventory().setItemInMainHand(itemInHand);
                            }
                            else {
                                player.sendMessage(ChatColor.WHITE + "enemy" + ChatColor.RED + " not found");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchHotbar(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int index = event.getNewSlot();
        ItemStack item = event.getPlayer().getInventory().getItem(index);
        if (item != null) {
            if (checkEqualsCompass(item)) {
                isHoldingTrackerCompass.add(player.getUniqueId());
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                BukkitTask task = scheduler.runTaskTimer(plugin, () -> {
                    trackTeam(player);
                }, 0, 20);
                tasks.put(player.getUniqueId(), task);
            }
            else
            {
                isHoldingTrackerCompass.remove(player.getUniqueId());
                BukkitTask task = tasks.remove(player.getUniqueId());
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }
}
