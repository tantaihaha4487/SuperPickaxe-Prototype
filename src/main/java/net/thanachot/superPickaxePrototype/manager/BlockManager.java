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
 * BlockManager (สำหรับ Paper / Bukkit)
 *
 * - แยกฟังก์ชันย่อย: ตรวจหา face จาก target-blocks, fallback ด้วย look vector,
 *   และคำนวณ 3x3 blocks ใน plane ตาม face ที่ได้
 *
 * หมายเหตุ: โค้ดนี้ใช้ API มาตรฐานของ Bukkit/Paper (player.getLastTwoTargetBlocks และ getRelative)
 * ถ้าต้องการความแม่นยำสูงขึ้น ให้เปลี่ยนไปใช้ rayTrace ของ Paper (player.rayTraceBlocks) แทน
 */
public final class BlockManager {

    // 2D offsets รอบศูนย์กลาง (ไม่รวม center)
    private static final int[][] DIRECTIONS = {
            { -1, -1 }, { 0, -1 }, { 1, -1 },
            { -1,  0 },           { 1,  0 },
            { -1,  1 }, { 0,  1 }, { 1,  1 }
    };

    private static final Set<Material> TRANSPARENT_MATERIALS = Set.of(
            Material.AIR, Material.CAVE_AIR, Material.WATER, Material.LAVA
    );

    private BlockManager() {
        // utility
    }

    /**
     * หาทิศทาง face ที่ผู้เล่นกำลังขุดในรูปแบบ Vector ที่มีค่า -1,0,1 บนแกนหนึ่งแกนเท่านั้น
     * ขั้นตอน:
     * 1) พยายามใช้ target-blocks (แม่นยำถ้าได้ผล)
     * 2) ถ้าไม่ได้ ให้ใช้ look vector เป็น fallback (เลือกแกนที่ absolute component ใหญ่สุด)
     *
     * คืนค่า: Vector ที่มีค่าแบบ (±1,0,0) หรือ (0,±1,0) หรือ (0,0,±1)
     */
    public static Vector getBreakingFace(Player player) {
        Vector v = getFaceFromTargetBlocks(player);
        if (v != null) return v;
        return getFaceFromLook(player);
    }

    /**
     * พยายามหา face โดยอาศัย last two target blocks (block adjacency)
     * คืน Vector หรือ null ถ้าไม่สามารถหาได้
     */
    public static Vector getFaceFromTargetBlocks(Player player) {
        List<Block> lastTwo = player.getLastTwoTargetBlocks(TRANSPARENT_MATERIALS, 10);
        if (lastTwo.size() >= 2) {
            Block target = lastTwo.get(1);   // block ที่ผู้เล่นเล็งอยู่ (ไกลกว่า)
            Block adjacent = lastTwo.get(0); // block ที่อยู่หน้า (ใกล้กว่า)
            BlockFace face = target.getFace(adjacent);
            if (face != null) {
                // ใช้ getModX/Y/Z เพื่อให้ได้ -1,0,1 ตามแกน
                return new Vector(face.getModX(), face.getModY(), face.getModZ());
            }
        }
        return null;
    }

    /**
     * Fallback: ใช้ทิศทางที่ผู้เล่นมอง (eye direction) และเลือกแกนที่มีค่าสัมบูรณ์มากที่สุด
     * คืน Vector แบบ (±1,0,0) หรือ (0,±1,0) หรือ (0,0,±1)
     */
    public static Vector getFaceFromLook(Player player) {
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
     * คืน list ของ block ใน 3x3 plane รอบ center ตาม face ที่ให้มา
     * - ถ้า face.x เป็นแกนโดดเด่น -> plane = YZ (vary Y,Z)
     * - ถ้า face.y เป็นแกนโดดเด่น -> plane = XZ (vary X,Z)
     * - ถ้า face.z เป็นแกนโดดเด่น -> plane = XY (vary X,Y)
     *
     * รวม center ไว้ด้วย (รวมเป็น 9 element)
     */
    public static List<Block> getAffectedBlocks(Block center, Vector face) {
        List<Block> blocks = new ArrayList<>(9);

        double absX = Math.abs(face.getX());
        double absY = Math.abs(face.getY());
        double absZ = Math.abs(face.getZ());

        boolean xDominant = absX > absY && absX > absZ;
        boolean yDominant = absY > absX && absY > absZ;
        boolean zDominant = absZ > absX && absZ > absY;

        // ถ้าไม่มีแกนไหนเด่น (tie) ให้ default เป็น Z-dominant (XY plane)
        if (!xDominant && !yDominant && !zDominant) {
            zDominant = true;
        }

        for (int[] off : DIRECTIONS) {
            int a = off[0];
            int b = off[1];
            Block relative;
            if (xDominant) {
                // plane YZ -> (0, a, b)
                relative = center.getRelative(0, a, b);
            } else if (yDominant) {
                // plane XZ -> (a, 0, b)
                relative = center.getRelative(a, 0, b);
            } else {
                // zDominant -> plane XY -> (a, b, 0)
                relative = center.getRelative(a, b, 0);
            }
            blocks.add(relative);
        }

        // add center as well
        blocks.add(center);
        return blocks;
    }
}
