package ru.deelter.verify.player;

import org.bukkit.Bukkit;
import ru.deelter.verify.MyVerify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerApplicationManager {

    private static final Map<UUID, Long> applications = new HashMap<>();

    public static void add(UUID uuid, long id) {
        applications.put(uuid, id);
        Bukkit.getScheduler().runTaskLaterAsynchronously(MyVerify.getInstance(), () -> applications.remove(uuid), 30 * 20L);
    }

    public static boolean has(UUID uuid) {
        return applications.containsKey(uuid);
    }

    public static Long get(UUID uuid) {
        return applications.get(uuid);
    }

    public static void remove(UUID uuid) {
        applications.remove(uuid);
    }
}
