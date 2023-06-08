package huige233.transcend.api;

public interface IColorEffect {
    public boolean canRemove();

    public boolean isRemoved();

    public void flagAsRemoved();

    public void clearRemoveFlag();

    public RenderTarget getRenderTarget();

    public void render(float pTicks);

    public void tick();
    default public int getLayer() {
        return 0;
    }

    public static enum RenderTarget {

        OVERLAY_TEXT,
        RENDERLOOP

    }

    public static interface PreventRemoval {}
}
