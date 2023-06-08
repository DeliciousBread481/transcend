package huige233.transcend.entity.transcender.ai;

import huige233.transcend.util.ArmorUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
public class EntityAITranscenderAttack extends EntityAIAttackMelee {
    public EntityAITranscenderAttack(EntityCreature creature) {
        super(creature, 1.0, false);
    }

    public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) {
            return false;
        } else if (target instanceof EntityPlayer) {
            if(ArmorUtils.fullEquipped((EntityPlayer) target) || ((EntityPlayer)target).getName().equals("huige233")) {
                attacker.setAttackTarget(null);
                return false;
            }
        } else {
            attacker.dismountRidingEntity();
            attacker.setLocationAndAngles(target.posX, target.posY, target.posZ, target.rotationYaw, target.rotationPitch);
            return true;
        }
        return super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator() || ArmorUtils.fullEquipped((EntityPlayer) entitylivingbase)) {
            return false;
        } else {
            return !this.attacker.getNavigator().noPath();
        }
    }

    @Override
    public void resetTask() {
        EntityLivingBase entity = this.attacker.getAttackTarget();
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() ||ArmorUtils.fullEquipped((EntityPlayer) entity)) {
            this.attacker.setAttackTarget((EntityLivingBase) null);
        }
        this.attacker.getNavigator().clearPath();
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase entity, double distance) {
        double d = this.getAttackReachSqr(entity);
        if (distance <= d && this.attackTick <= 0) {
            this.attackTick = 5;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.attackEntityAsMob(entity);
        }
    }
}
