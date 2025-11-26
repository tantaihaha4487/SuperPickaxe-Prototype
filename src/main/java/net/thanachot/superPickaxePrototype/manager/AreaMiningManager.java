package net.thanachot.superPickaxePrototype.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manager for handling area mining calculations (e.g. 3x3 breaking).
 */
public class AreaMiningManager {

    private static final Set<Material> TRANSPARENT_MATERIALS = Set.of(
            Material.AIR, Material.CAVE_AIR, Material.WATER, Material.LAVA, Material.LIGHT);

    /**
     * Determines the face of the block the player is looking at.
     * Tries to use ray-tracing first, falls back to look direction.
     */
    public static Vector getBreakingFace(Player player) {
        List<Block> lastTwo = player.getLastTwoTargetBlocks(TRANSPARENT_MATERIALS, 10);
        if (lastTwo.size() == 2) {
            Block target = lastTwo.get(1);
            Block adjacent = lastTwo.get(0);
            BlockFace face = target.getFace(adjacent);
            if (face != null) {
                return new Vector(face.getModX(), face.getModY(), face.getModZ());
            }
        }
        return getFaceFromLookDirection(player);
    }

    private static Vector getFaceFromLookDirection(Player player) {
        Vector look = player.getEyeLocation().getDirection();
        double x = Math.abs(look.getX());
        double y = Math.abs(look.getY());
        double z = Math.abs(look.getZ());

        if (x >= y && x >= z)
            return new Vector(Math.signum(look.getX()), 0, 0);
        if (y >= x && y >= z)
            return new Vector(0, Math.signum(look.getY()), 0);
        return new Vector(0, 0, Math.signum(look.getZ()));
    }

    /**
     * Returns a list of blocks in a square area centered on the given block,
     * perpendicular to the given face.
     *
     * @param center The center block
     * @param face   The face vector (normal to the plane)
     * @param size   The size of the square (must be odd, e.g., 3 for 3x3)
     * @return List of blocks in the area
     */
    public static List<Block> getAffectedBlocks(Block center, Vector face, int size) {
        if (size < 1 || size % 2 == 0) {
            throw new IllegalArgumentException("Size must be odd and >= 1");
        }

        List<Block> blocks = new ArrayList<>(size * size);
        int radius = (size - 1) / 2;

        // Determine the plane axes based on the face normal
        // If face is X, plane is YZ. If face is Y, plane is XZ. If face is Z, plane is
        // XY.
        double x = Math.abs(face.getX());
        double y = Math.abs(face.getY());

        // We iterate from -radius to +radius for two dimensions
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (x > 0.5) { // X-axis dominant -> YZ plane
                    blocks.add(center.getRelative(0, i, j));
                } else if (y > 0.5) { // Y-axis dominant -> XZ plane
                    blocks.add(center.getRelative(i, 0, j));
                } else { // Z-axis dominant -> XY plane
                    blocks.add(center.getRelative(i, j, 0));
                }
            }
        }

        return blocks;
    }
}
