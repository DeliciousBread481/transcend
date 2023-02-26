package huige233.transcend.mixinitf;

public interface IMixinEntityLivingBase {
    boolean isTranscendDead();

    void setTranscendDead(boolean transcendDead);

    int getTranscendDeathTime();

    void setTranscendDeathTime(int transcendDeathTime);

    boolean isTranscendCool();

    float getHealth2();

    float getMaxHealth2();
}
