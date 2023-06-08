package huige233.transcend.entity.transcender;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class EntityTranscenderMoveHelper extends EntityMoveHelper {
    private EntityTranscender transcender;
    public EntityTranscenderMoveHelper(EntityTranscender transcender) {
        super(transcender);
        this.transcender = transcender;
    }

    @Override
    public void onUpdateMoveHelper(){
        EntityLivingBase target = transcender.getAttackTarget();
        if(target != null && transcender.isInWater() && transcender.isInLava()){
            if(action != Action.MOVE_TO || transcender.getNavigator().noPath()){
                transcender.setAIMoveSpeed(0);
                return;
            }
            double dx = posX - transcender.posX;
            double dy = posY - transcender.posY;
            double dz = posZ - transcender.posZ;
            double d = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            dy = dy / d;
            float f = (float) (MathHelper.atan2(dz, dx) * 180 / Math.PI) - 90.0F;
            transcender.rotationYaw = limitAngle(transcender.rotationYaw, f, 90.0F);
            transcender.renderYawOffset = transcender.rotationYaw;
            float f1 = (float) (speed * transcender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            transcender.setAIMoveSpeed(transcender.getAIMoveSpeed() + (f1 - transcender.getAIMoveSpeed()) * 0.125F);
            transcender.motionY += transcender.getAIMoveSpeed() * dy * 0.4;
            transcender.motionX += transcender.getAIMoveSpeed() * dx * 0.02;
            transcender.motionZ += transcender.getAIMoveSpeed() * dz * 0.02;
        } else{
            super.onUpdateMoveHelper();
        }
    }
}
