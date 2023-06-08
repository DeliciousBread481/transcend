package huige233.transcend.entity.renderer;


import huige233.transcend.entity.EntityColorParticle;
import huige233.transcend.entity.EntityColore;
import huige233.transcend.util.Vector.Vector3;

import java.awt.*;

public class EffectHelper {
    public static EntityColorParticle genericFlareParticle(Vector3 v) {
        return genericFlareParticle(v.getX(), v.getY(), v.getZ());
    }

    public static EntityColorParticle genericFlareParticle(double x, double y, double z) {
        EntityColorParticle p = new EntityColorParticle(x, y, z);
        p.enableAlphaFade(EntityColore.AlphaFunction.PYRAMID).setAlphaMultiplier(0.75F).setColor(new Color(60, 0, 255));
        EffectHandler.getInstance().registerFX(p);
        return p;
    }

    public static EntityColorParticle genericGatewayFlareParticle(double x, double y, double z) {
        EntityColorParticle p = new EntityColorParticle(x, y, z);
        p.enableAlphaFade(EntityColore.AlphaFunction.PYRAMID).setAlphaMultiplier(0.75F).setColor(new Color(60, 0, 255));
        EffectHandler.getInstance().registerFX(p);
        return p;
    }
}
