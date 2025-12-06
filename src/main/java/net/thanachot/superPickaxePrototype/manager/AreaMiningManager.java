package net.thanachot.superPickaxePrototype.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manager for handling area mining calculations (e.g. 3x3 breaking).
 */
public class AreaMiningManager {

    private static final Set<Material> TRANSPARENT_MATERIALS = Set.of(
            Material.AIR, Material.CAVE_AIR, Material.WATER, Material.LAVA, Material.LIGHT);

    private static final int RAY_TRACE_DISTANCE = 10;
    private static final double AXIS_THRESHOLD = 0.5;

    /**
     * Determines the face of the block the player is breaking.
     * Uses ray-tracing first, falls back to player look direction if ray-trace
     * fails.
     * 
     * @param player the player breaking the block
     * @return a normalized vector representing the breaking face
     */
    @NotNull
    public static Vector getBreakingFace(@NotNull Player player) {
        Vector rayTraceFace = getRayTracedFace(player);
        return rayTraceFace != null ? rayTraceFace : getFaceFromLookDirection(player);
    }

    /**
     * Attempts to determine the breaking face using ray-tracing.
     * 
     * @param player the player
     * @return the face vector, or null if ray-trace failed
     */
    private static Vector getRayTracedFace(@NotNull Player player) {
        List<Block> lastTwoBlocks = player.getLastTwoTargetBlocks(TRANSPARENT_MATERIALS, RAY_TRACE_DISTANCE);

        if (lastTwoBlocks.size() != 2) {
            return null;
        }

        Block targetBlock = lastTwoBlocks.get(1);
        Block adjacentBlock = lastTwoBlocks.get(0);
        BlockFace face = targetBlock.getFace(adjacentBlock);

        if (face == null) {
            return null;
        }

        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * Determines the breaking face from player's look direction.
     * Returns the dominant axis direction (X, Y, or Z).
     * 
     * @param player the player
     * @return a normalized vector for the dominant axis
     */
    @NotNull
    private static Vector getFaceFromLookDirection(@NotNull Player player) {
        Vector lookDirection = player.getEyeLocation().getDirection();

        double absX = Math.abs(lookDirection.getX());
        double absY = Math.abs(lookDirection.getY());
        double absZ = Math.abs(lookDirection.getZ());

        // Find the dominant axis and return its sign
        if (absX >= absY && absX >= absZ) {
            return new Vector(Math.signum(lookDirection.getX()), 0, 0);
        }

        if (absY >= absX && absY >= absZ) {
            return new Vector(0, Math.signum(lookDirection.getY()), 0);
        }

        return new Vector(0, 0, Math.signum(lookDirection.getZ()));
    }

    /**
     * Returns a list of blocks in a square area centered on the given block,
     * perpendicular to the given face.
     *
     * @param centerBlock The center block
     * @param faceNormal  The face vector (normal to the plane)
     * @param size        The size of the square (must be odd, e.g., 3 for 3x3)
     * @return List of blocks in the area
     * @throws IllegalArgumentException if size is not odd or less than 1
     */
    @NotNull
    public static List<Block> getAffectedBlocks(@NotNull Block centerBlock,
            @NotNull Vector faceNormal,
            int size) {
        if (size < 1 || size % 2 == 0) {
            throw new IllegalArgumentException("Size must be odd and >= 1, got: " + size);
        }

        int radius = (size - 1) / 2;
        List<Block> blocks = new ArrayList<>(size * size);

        // Determine which plane to use based on the face normal
        PlaneType planeType = determinePlaneType(faceNormal);

        // Generate blocks in a square pattern on the determined plane
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                blocks.add(getRelativeBlock(centerBlock, planeType, i, j));
            }
        }

        return blocks;
    }

    /**
     * Determines which plane (YZ, XZ, or XY) to use based on the face normal.
     */
    @NotNull
    private static PlaneType determinePlaneType(@NotNull Vector faceNormal) {
        double absX = Math.abs(faceNormal.getX());
        double absY = Math.abs(faceNormal.getY());

        if (absX > AXIS_THRESHOLD) {
            return PlaneType.YZ; // X-axis dominant -> YZ plane
        } else if (absY > AXIS_THRESHOLD) {
            return PlaneType.XZ; // Y-axis dominant -> XZ plane
        } else {
            return PlaneType.XY; // Z-axis dominant -> XY plane
        }
    }

    /**
     * Gets a block relative to the center based on the plane type.
     */
    @NotNull
    private static Block getRelativeBlock(@NotNull Block center,
            @NotNull PlaneType planeType,
            int offset1,
            int offset2) {
        return switch (planeType) {
            case YZ -> center.getRelative(0, offset1, offset2);
            case XZ -> center.getRelative(offset1, 0, offset2);
            case XY -> center.getRelative(offset1, offset2, 0);
        };
    }

    /**
     * Represents the three possible planes for area mining.
     */
    private enum PlaneType {
        YZ, // Perpendicular to X-axis
        XZ, // Perpendicular to Y-axis
        XY // Perpendicular to Z-axis
    }
}
