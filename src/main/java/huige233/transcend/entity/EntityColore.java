package huige233.transcend.entity;


import huige233.transcend.api.IColorEffect;
import huige233.transcend.util.EntityUtils;
import huige233.transcend.util.Vector.Vector3;
import net.minecraft.entity.Entity;

import java.util.function.Function;

public abstract class EntityColore implements IColorEffect {
    private static long counter = 0;

    public final long id;
    protected int age = 0;
    protected int maxAge = 40;
    protected boolean removeRequested = false;

    private boolean flagRemoved = true;

    public EntityColore() {
        this.id = counter;
        counter++;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean canRemove() {
        return age >= maxAge || removeRequested;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        age++;
    }

    public void requestRemoval() {
        this.removeRequested = true;
    }

    public boolean isRemoved() {
        return flagRemoved;
    }

    public void flagAsRemoved() {
        flagRemoved = true;
        removeRequested = false;
    }

    public void clearRemoveFlag() {
        flagRemoved = false;
    }

    public static interface RenderAlphaFunction<T extends IColorEffect> {

        public float getRenderAlpha(T fx, float currentAlpha);

    }

    public static enum AlphaFunction {

        CONSTANT,
        FADE_OUT,
        PYRAMID;

        AlphaFunction() {}

        public float getAlpha(int age, int maxAge) {
            switch (this) {
                case CONSTANT:
                    return 1F;
                case FADE_OUT:
                    return 1F - (((float) age) / ((float) maxAge));
                case PYRAMID:
                    float halfAge = maxAge / 2F;
                    return 1F - (Math.abs(halfAge - age) / halfAge);
                default:
                    break;
            }
            return 1F;
        }

    }

    public static interface RenderOffsetController {

        public Vector3 changeRenderPosition(EntityColore fx, Vector3 currentRenderPos, Vector3 currentMotion, float pTicks);

    }

    public static interface PositionController<T extends IColorEffect> {

        public Vector3 updatePosition(T fx, Vector3 position, Vector3 motionToBeMoved);

    }

    public static interface MotionController<T extends IColorEffect> {

        public Vector3 updateMotion(T fx, Vector3 motion);

        public static class EntityTarget<T extends IColorEffect> implements EntityColore.MotionController<T> {

            private final Entity target;
            private final Function<T, Vector3> positionFunction;

            public EntityTarget(Entity target, Function<T, Vector3> positionFunction) {
                this.target = target;
                this.positionFunction = positionFunction;
            }

            @Override
            public Vector3 updateMotion(T fx, Vector3 motion) {
                if (target.isDead) return motion;
                EntityUtils.applyVortexMotion((v) -> positionFunction.apply(fx), motion::add, Vector3.atEntityCenter(target), 256, 1);
                return motion.multiply(0.9);
            }

        }

    }

    public static interface ScaleFunction<T extends IColorEffect> {

        public static final EntityColore.ScaleFunction<IColorEffect> IDENTITY = (EntityColore.ScaleFunction<IColorEffect>) (fx, pos, pTicks, scaleIn) -> scaleIn;

        public float getScale(T fx, Vector3 pos, float pTicks, float scaleIn);

        public static class Shrink<T extends EntityColore> implements EntityColore.ScaleFunction<T> {
            @Override
            public float getScale(T fx, Vector3 pos, float pTicks, float scaleIn) {
                float prevAge = Math.max(0F, ((float) fx.getAge() - 1)) / ((float) fx.getMaxAge());
                float currAge = Math.max(0F, ((float) fx.getAge()))     / ((float) fx.getMaxAge());
                return (float) (scaleIn * (1 - (interpolate(prevAge, currAge, pTicks))));
            }

        }
    }

    public static interface RefreshFunction {
        public boolean shouldRefresh();
    }

    public static Vector3 interpolatePosition(Entity e, float partialTicks) {
        return new Vector3(
                interpolate(e.lastTickPosX, e.posX, partialTicks),
                interpolate(e.lastTickPosY, e.posY, partialTicks),
                interpolate(e.lastTickPosZ, e.posZ, partialTicks)
        );
    }

    public static double interpolate(double oldP, double newP, float partialTicks) {
        if(oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }
}
