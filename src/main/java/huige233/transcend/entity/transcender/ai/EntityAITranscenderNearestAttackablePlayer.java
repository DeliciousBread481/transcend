package huige233.transcend.entity.transcender.ai;

import huige233.transcend.util.ArmorUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAITranscenderNearestAttackablePlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
    public EntityAITranscenderNearestAttackablePlayer(EntityCreature creature) {
        super(creature, EntityPlayer.class, false);
    }

    public boolean shouldExecute() {
        targetEntity = null;
        double min = Double.MAX_VALUE;
        for (EntityPlayer player : taskOwner.world.playerEntities) {
            if (!player.isSpectator() && ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")) {
                double d = player.getDistanceSq(taskOwner);
                if (d < min && d < getTargetDistance()) {
                    min = d;
                    targetEntity = player;
                }
            }

            return targetEntity != null;
        }
        return false;
    }
}
