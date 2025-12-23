package org.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class RiftManager {

    private final World world;
    private final Random random = new Random();

    // Size = 10,000 x 10,000 → radius = 5000
    private static final int RIFT_RADIUS = 2500;

    public RiftManager(World world) {
        this.world = world;
    }

    /**
     * Generates a random location for a rift inside the 10k x 10k region.
     * Returns null if no valid spot found after many attempts.
     */
    public Location findRiftSpawnLocation() {
        Location spawn = world.getSpawnLocation();
        double sx = spawn.getX();
        double sz = spawn.getZ();

        for (int tries = 0; tries < 30; tries++) {

            // --- Pick random X and Z in the 10k x 10k area ---
            double x = sx - RIFT_RADIUS + random.nextInt(RIFT_RADIUS * 2);
            double z = sz - RIFT_RADIUS + random.nextInt(RIFT_RADIUS * 2);

            // --- Find the top solid block ---
            int y = world.getHighestBlockYAt((int) x, (int) z);

            Location candidate = new Location(world, x, y, z);

            // --- Validate location ---
            if (!isValidRiftLocation(candidate)) continue;

            return candidate;
        }

        return null; // No good location found
    }

    /**
     * Check if a location is suitable for a rift.
     */
    private boolean isValidRiftLocation(Location loc) {
        Material block = loc.getBlock().getType();

        // No water, no lava
        if (block == Material.WATER || block == Material.LAVA) return false;

        // Not too steep: compare Y difference with neighbors
        Location l1 = loc.clone().add(1, 0, 0);
        Location l2 = loc.clone().add(-1, 0, 0);
        Location l3 = loc.clone().add(0, 0, 1);
        Location l4 = loc.clone().add(0, 0, -1);

        int y = loc.getBlockY();
        if (Math.abs(l1.getBlockY() - y) > 2) return false;
        if (Math.abs(l2.getBlockY() - y) > 2) return false;
        if (Math.abs(l3.getBlockY() - y) > 2) return false;
        if (Math.abs(l4.getBlockY() - y) > 2) return false;

        return true;
    }
}
