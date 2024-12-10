package org.ajls.tractorcompass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class BukkitTaskUtils {
    public static void removeTask(UUID uuid, HashMap<UUID, BukkitTask> hashMap) {
        BukkitTask task = hashMap.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
}
