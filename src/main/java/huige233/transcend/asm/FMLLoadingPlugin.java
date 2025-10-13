package huige233.transcend.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(10)
@IFMLLoadingPlugin.Name("transcend")
@IFMLLoadingPlugin.TransformerExclusions("huige233.transcend.asm.")
public class FMLLoadingPlugin implements IFMLLoadingPlugin {

    static {
        try {
            addTweaker();
        } catch (Throwable t) {
            System.err.println("[Transcend] Failed to add Tweaker: " + t);
            t.printStackTrace();
        }
    }

    private static void addTweaker() throws Exception {
        List<String> classes = (List<String>) Launch.blackboard.get("TweakClasses");
        if (!classes.contains("huige233.transcend.asm.tweaker.Tweaker")) {
            classes.add("huige233.transcend.asm.tweaker.Tweaker");
            System.out.println("[Transcend] Tweaker registered: huige233.transcend.asm.tweaker.Tweaker");
        }

        URL url = FMLLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation();
        ClassLoader cl = Launch.classLoader;

        try {
            Method addURL = cl.getClass().getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(cl, url);
            System.out.println("[Transcend] CoreMod JAR injected into LaunchClassLoader.");
        } catch (Throwable t) {
            System.err.println("[Transcend] Failed to inject via LaunchClassLoader, fallback to system loader.");
            Method addURL2 = java.net.URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL2.setAccessible(true);
            addURL2.invoke(ClassLoader.getSystemClassLoader(), url);
        }

        try {
            Class<?> tweakerClass = Class.forName("huige233.transcend.asm.tweaker.Tweaker", true, cl);
            System.out.println("[Transcend] Tweaker class preloaded successfully: " + tweakerClass);
        } catch (Throwable preloadErr) {
            System.err.println("[Transcend] Failed to preload Tweaker class: " + preloadErr);
        }
    }


    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "huige233.transcend.asm.ClassTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // 环境标记
        boolean runtimeDeobf = (boolean) data.get("runtimeDeobfuscationEnabled");
        huige233.transcend.asm.RemapUtils.isDevelopmentEnvironment = !runtimeDeobf;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
