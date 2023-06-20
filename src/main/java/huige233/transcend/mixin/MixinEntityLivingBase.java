package huige233.transcend.mixin;

import huige233.transcend.mixinitf.IMixinEntityLivingBase;
import huige233.transcend.util.EventUtil;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements IMixinEntityLivingBase {
    private boolean transcendDead;
    private int transcendDeathTime;
    private boolean transcendCool;

    @Override
    public boolean isTranscendDead() {
        return transcendDead;
    }

    @Override
    public void setTranscendDead(boolean transcendDead) {
        this.transcendDead = transcendDead;
    }

    @Override
    public int getTranscendDeathTime() {
        return transcendDeathTime;
    }

    @Override
    public void setTranscendDeathTime(int transcendDeathTime) {
        this.transcendDeathTime = transcendDeathTime;
    }

    @Override
    public boolean isTranscendCool() {
        return transcendCool;
    }

    @Overwrite
    //final?
    public float getHealth() {
        return EventUtil.getHealth((EntityLivingBase) (Object) this);
    }

    @Overwrite
    public float getMaxHealth() {
        return EventUtil.getMaxHealth((EntityLivingBase) (Object) this);
    }


}
