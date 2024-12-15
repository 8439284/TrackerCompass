package org.ajls.tractorcompass;

import org.ajls.lib.utils.ScoreboardU;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ScoreboardsAndTeams {
    //create scoreboard
    public static void createScoreboard(String name) {//@Nullable String type, @Nullable String displayname
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name) == null) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getMainScoreboard();
            Objective objective = board.registerNewObjective(name, "dummy");
        }
    }

    public static Objective getScoreboard(String name){
        //get the scoreboard of this name
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name);
        return objective;
    }

    public static void registerTeam(String teamName){
        // register team
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName) == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
        }
    }

    public static void setTeamRule(String teamName, Team.Option option, Team.OptionStatus optionStatus){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Team team = scoreboardManager.getMainScoreboard().getTeam(teamName);
        team.setOption(option,optionStatus);
    }

    public static void setFriendlyFire(String teamName, boolean friendlyFire){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Team team = scoreboardManager.getMainScoreboard().getTeam(teamName);
        team.setAllowFriendlyFire(friendlyFire);
    }

    public static void setTeamColor(String teamName, ChatColor chatColor){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Team team = scoreboardManager.getMainScoreboard().getTeam(teamName);
        team.setColor(chatColor);
    }

    public static Team getTeam(String teamName){
        //get the team of this name
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        return team;
    }

    public static ChatColor getTeamColor(String teamName){
        Team team = getTeam(teamName);
        return team.getColor();
    }

    public static Team getPlayerTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getPlayerTeam(player);
        return team;
    }

    public static String getPlayerTeamName(Player player) {
//        return getPlayerTeam(player).getName();
        return ScoreboardU.getPlayerTeamName(player);
    }




}

