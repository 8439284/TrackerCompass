package org.ajls.tractorcompass.dataContainer;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class TrackTeamResults { //return type of trackTeam method
    boolean success;
    Location targetLocation;
    String trackedTeamName;
    ChatColor trackedTeamColor;
    public TrackTeamResults(boolean success, Location targetLocation, String trackedTeamName, ChatColor trackedTeamColor) {}
}
