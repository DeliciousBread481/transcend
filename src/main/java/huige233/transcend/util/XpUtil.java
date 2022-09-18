package huige233.transcend.util;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

public class XpUtil {
    public static float getAllXp(int level,float xp){
        if(level<=16){
            return (level * level + xp);
        } else if (level <=31) {
            return (float) (2.5* level * level - 40.5 * level + 360 + xp);
        } else {
            return (float) (4.5 * level * level - 162.5 * level + 2200 + xp);
        }
    }

    public static final int XP_PER_BOTTLE = 8;
    public static final int RATIO = 20;
    public static final int LIQUID_PER_XP_BOTTLE = XP_PER_BOTTLE * RATIO;

    public static int liquidToExperience(int liquid) {
        return liquid / RATIO;
    }

    public static int experienceToLiquid(int xp) {
        return xp * RATIO;
    }

    public static int getLiquidForLevel(int level) {
        return experienceToLiquid(getExperienceForLevel(level));
    }

    private static final int[] xpmap = new int[21863];

    public static int getExperienceForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (level >= 21863) {
            return Integer.MAX_VALUE;
        }
        return xpmap[level];
    }

    static {
        int res = 0;
        for (int i = 0; i < 21863; i++) {
            res += getXpBarCapacity(i);
            if (res < 0) {
                res = Integer.MAX_VALUE;
            }
            xpmap[i] = res;
        }
    }

    public static int getXpBarCapacity(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        }
        return 7 + level * 2;
    }

    public static int getLevelForExperience(int experience) {
        for (int i = 1; i < xpmap.length; i++) {
            if (xpmap[i] > experience) {
                return i - 1;
            }
        }
        return xpmap.length;
    }

    public static int getPlayerXP(@Nonnull EntityPlayer player) {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static void addPlayerXP(@Nonnull EntityPlayer player, int amount) {
        int experience = Math.max(0, getPlayerXP(player) + amount);
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }
}
