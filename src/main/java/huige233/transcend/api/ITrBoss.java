package huige233.transcend.api;

import java.util.UUID;

public interface ITrBoss {
    public UUID getBoosInfoUuid();
    public default BossType getBossType(){
        return BossType.NORMAL_BOSS;
    }
}
