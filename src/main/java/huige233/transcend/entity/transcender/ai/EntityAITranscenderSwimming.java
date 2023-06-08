package huige233.transcend.entity.transcender.ai;

import huige233.transcend.entity.transcender.EntityTranscender;
import net.minecraft.entity.ai.EntityAISwimming;

public class EntityAITranscenderSwimming extends EntityAISwimming {
    private EntityTranscender transcender;
    private boolean obstructed;
    public EntityAITranscenderSwimming(EntityTranscender transcender){
        super(transcender);
        this.transcender = transcender;
    }
    public boolean shouldContinueExecuting(){
        return this.shouldExecute() && !this.obstructed;
    }
    @Override
    public void updateTask(){
        if(transcender.getNavigator().noPath() && transcender.getAttackingEntity() == null){
            super.updateTask();
        }
    }
    @Override
    public void startExecuting() {
        obstructed = false;
    }
}
