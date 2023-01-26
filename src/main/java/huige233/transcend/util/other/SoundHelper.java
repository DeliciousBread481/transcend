package huige233.transcend.util.other;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SoundHelper {
    private SoundHelper() {
    }

    public static final Vec3d BLOCK_CENTER = new Vec3d(.5, .5, .5);
    public static final Vec3d BLOCK_TOP = new Vec3d(.5, 1, .5);
    public static final Vec3d BLOCK_LOW = new Vec3d(.5, .1, .5);

    /**
     * Plays a sound at the given location. If called on a server, it will play it for all players.
     *
     */
    public static void playSound(World world, double soundLocationX, double soundLocationY, double soundLocationZ, IModSound sound, float volume, float pitch) {
        if (sound != null && sound.isValid()) {
            if (world instanceof WorldServer) {
                world.playSound(null, soundLocationX, soundLocationY, soundLocationZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch);
            } else {
                world.playSound(soundLocationX, soundLocationY, soundLocationZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch, false);
            }
        }
    }

    /**
     * Plays a sound at the center of the given BlockPos. If called on a server, it will play it for all players.
     *
     */
    public static void playSound(World world, BlockPos soundLocation, IModSound sound, float volume, float pitch) {
        playSound(world, soundLocation, BLOCK_CENTER, sound, volume, pitch);
    }

    /**
     * Plays a sound at an offset to the given BlockPos. If called on a server, it will play it for all players.
     * <p>
     *
     * See {@link SoundHelper#BLOCK_CENTER}, {@link SoundHelper#BLOCK_TOP} and {@link SoundHelper#BLOCK_LOW} for common offsets.
     *
     */
    public static void playSound(World world, BlockPos soundLocation, Vec3d offset, IModSound sound, float volume, float pitch) {
        playSound(world, soundLocation.getX() + offset.x, soundLocation.getY() + offset.y, soundLocation.getZ() + offset.z, sound, volume, pitch);
    }

    /**
     * Plays a sound at the location of given entity. If called on a server, it will play it for all players.
     *
     */
    public static void playSound(World world, Entity soundLocation, IModSound sound, float volume, float pitch) {
        playSound(world, soundLocation.posX, soundLocation.posY, soundLocation.posZ, sound, volume, pitch);
    }
}
