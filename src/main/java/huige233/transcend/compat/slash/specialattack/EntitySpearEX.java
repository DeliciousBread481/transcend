package huige233.transcend.compat.slash.specialattack;

import huige233.transcend.util.HyperEntitySelector;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.EntitySpearManager;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class EntitySpearEX extends EntitySpearManager {
    private double dRustDist = 3.0d;

    public EntitySpearEX(World par1World) {
        super(par1World);
    }

    public EntitySpearEX(World par1World, EntityLivingBase entityLiving) {
        super(par1World, entityLiving);
    }

    public EntitySpearEX(World par1World, EntityLivingBase entityLiving, boolean multiHit) {
        super(par1World, entityLiving, multiHit);
    }

    public EntitySpearEX(World par1World, EntityLivingBase entityLiving, boolean multiHit, double rushDist) {
        this(par1World, entityLiving, multiHit);
        dRustDist = rushDist;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            Entity thrower = this.getThrower();
            if (thrower != null) {

                AxisAlignedBB bb = new AxisAlignedBB(thrower.posX - dRustDist, thrower.posY - dRustDist,
                        thrower.posZ - dRustDist, thrower.posX + dRustDist, thrower.posY + dRustDist,
                        thrower.posZ + dRustDist);
                if (thrower instanceof EntityLivingBase) {
                    EntityLivingBase entityLiving = (EntityLivingBase) thrower;
                    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb,
                            HyperEntitySelector.getInstance());
                    list.removeAll(alreadyHitEntity);
                    StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.DestructObject);

                    for (Entity curEntity : list) {
                        if (blade.isEmpty())
                            break;
                        ReflectionAccessHelper.setVelocity(curEntity, 0, 0, 0);
                        if (curEntity instanceof EntityLivingBase) {
                            EntityLivingBase curLivingEntity = (EntityLivingBase) curEntity;
                            curLivingEntity.setHealth(0);
                        }
                    }
                    StylishRankManager.doAttack(this.thrower);

                }

            }
        }

        if (ticksExisted >= getLifeTime()) {
            alreadyHitEntity.clear();
            alreadyHitEntity = null;
            setDead();
        }
    }
}
