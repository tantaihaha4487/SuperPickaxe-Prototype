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
 * BlockManager (refactored)
 * - getBreakingFace: same as before
 * - getAffectedBlocks(center, face, size): returns NxN blocks where size is odd (3,5,7,...)
 */
public class BlockManager {

    private static final Set<Material> TRANSPARENT_MATERIALS = Set.of(
            Material.AIR, Material.CAVE_AIR, Material.WATER, Material.LAVA
    );

    public static Vector getBreakingFace(Player player) {
        Vector fromTarget = getFaceFromTargetBlocks(player);
        if (fromTarget != null) return fromTarget;
        return getFaceFromLookDirection(player);
    }

    /* ---------------- Face detection (unchanged) ---------------- */

    public static Vector getFaceFromTargetBlocks(Player player) {
        List<Block> lastTwo = player.getLastTwoTargetBlocks(TRANSPARENT_MATERIALS, 10);
        if (lastTwo.size() < 2) return null;
        Block target = lastTwo.get(1);
        Block adjacent = lastTwo.get(0);
        BlockFace face = target.getFace(adjacent);
        if (face == null) return null;
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    public static Vector getFaceFromLookDirection(Player player) {
        Vector look = player.getEyeLocation().getDirection();
        double ax = Math.abs(look.getX()), ay = Math.abs(look.getY()), az = Math.abs(look.getZ());
        if (ax >= ay && ax >= az) {
            return new Vector((int) Math.signum(look.getX()), 0, 0);
        } else if (ay >= ax && ay >= az) {
            return new Vector(0, (int) Math.signum(look.getY()), 0);
        } else {
            return new Vector(0, 0, (int) Math.signum(look.getZ()));
        }
    }

    /**
     * Return NxN blocks centred on `center` in the plane perpendicular to `face`.
     * `size` must be an odd integer >= 1 (3 -> 3x3, 5 -> 5x5).
     */
    public static List<Block> getAffectedBlocks(Block center, Vector face, int size) {
        if (size < 1 || size % 2 == 0) throw new IllegalArgumentException("size must be odd and >= 1");
        Axis axis = dominantAxis(face);
        int radius = (size - 1) / 2;

        // generate 2D offsets for NxN grid
        List<int[]> offsets2D = generateOffsets2D(radius);

        // map 2D offsets to world blocks according to axis
        return mapOffsetsToBlocks(center, axis, offsets2D);
    }

    /* ---------------- Refactored affected-blocks ---------------- */

    // Determine which axis is dominant from the face vector
    private static Axis dominantAxis(Vector face) {
        double ax = Math.abs(face.getX());
        double ay = Math.abs(face.getY());
        double az = Math.abs(face.getZ());

        if (ax > ay && ax > az) return Axis.X;
        if (ay > ax && ay > az) return Axis.Y;
        // default to Z if no strict winner
        return Axis.Z;
    }

    // Generate 2D offsets for a square with given radius (radius=1 -> offsets -1..1)
    // Offsets include the center (0,0)
    private static List<int[]> generateOffsets2D(int radius) {
        List<int[]> offsets = new ArrayList<>((2 * radius + 1) * (2 * radius + 1));
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                offsets.add(new int[]{dx, dy}); // dx, dy pair
            }
        }
        return offsets;
    }

    // Map 2D offsets (dx,dy) to actual Blocks relative to center depending on axis:
    //  - X axis dominant -> plane is YZ => (0, dy, dx)
    //  - Y axis dominant -> plane is XZ => (dx, 0, dy)
    //  - Z axis dominant -> plane is XY => (dx, dy, 0)
    private static List<Block> mapOffsetsToBlocks(Block center, Axis axis, List<int[]> offsets2D) {
        List<Block> list = new ArrayList<>(offsets2D.size());
        for (int[] o : offsets2D) {
            int dx = o[0];
            int dy = o[1];
            Block relative = switch (axis) {
                case X -> // vary Y (dy) and Z (dx)
                        center.getRelative(0, dy, dx);
                case Y -> // vary X (dx) and Z (dy)
                        center.getRelative(dx, 0, dy);
                default -> // Z: vary X (dx) and Y (dy)
                        center.getRelative(dx, dy, 0);
            };
            list.add(relative);
        }
        return list;
    }

    public enum Axis {X, Y, Z}
}
