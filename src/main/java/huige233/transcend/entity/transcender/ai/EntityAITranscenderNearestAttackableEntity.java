package huige233.transcend.entity.transcender.ai;

import huige233.transcend.entity.transcender.EntityTranscender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import java.util.Collections;
import java.util.List;

public class EntityAITranscenderNearestAttackableEntity extends EntityAINearestAttackableTarget<EntityLivingBase>{
    public EntityAITranscenderNearestAttackableEntity(EntityCreature creature) {
        super(creature, EntityLivingBase.class, false);
    }

    public boolean shouldExecute() {
        List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(this.targetClass, getTargetableArea(getTargetDistance()), targetEntitySelector);
        list.removeIf(entity -> entity instanceof EntityTranscender);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.sorter);
            targetEntity = list.get(0);
            return true;
        }
    }
}
