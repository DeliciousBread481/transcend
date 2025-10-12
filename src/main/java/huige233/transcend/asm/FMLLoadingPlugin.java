package huige233.transcend.asm;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(10)
@IFMLLoadingPlugin.Name("transcend")
@IFMLLoadingPlugin.TransformerExclusions("huige233.transcend.asm.")
public class FMLLoadingPlugin implements IFMLLoadingPlugin {

    static {
        addTweaker();
    }

    private static void addTweaker() {
        try {
            List<String> classes = (List<String>) Launch.blackboard.get("TweakClasses");
            classes.add("huige233.transcend.asm.tweaker.Tweaker");

            URL url = FMLLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation();

            try {
                Method addURLMethod = Launch.classLoader.getClass().getDeclaredMethod("addURL", URL.class);
                addURLMethod.setAccessible(true);
                addURLMethod.invoke(Launch.classLoader, url);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                try {
                    Method addURLMethod2 = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addURLMethod2.setAccessible(true);
                    ClassLoader sysLoader = ClassLoader.getSystemClassLoader();
                    if (addURLMethod2.getDeclaringClass().isInstance(sysLoader)) {
                        addURLMethod2.invoke(sysLoader, url);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to add Tweaker", t);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"huige233.transcend.asm.ClassTransformer"};
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
        RemapUtils.isDevelopmentEnvironment = !(boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
