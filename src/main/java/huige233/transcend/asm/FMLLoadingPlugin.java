package huige233.transcend.asm;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(10)
@IFMLLoadingPlugin.Name("transcend")
@IFMLLoadingPlugin.TransformerExclusions("huige233.transcend.asm.")
public class FMLLoadingPlugin implements IFMLLoadingPlugin{
    static {
        addTweaker();
    }

    private static void addTweaker() {
        List<String> classes = (List<String>) Launch.blackboard.get("TweakClasses");
        classes.add("huige233.transcend.asm.tweaker.Tweaker");

        try {
            Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
            addURLMethod.invoke(ClassLoader.getSystemClassLoader(), FMLLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public String[] getASMTransformerClass(){
        //"huige233.transcend.asm.ClassTransformer"
        return new String[] { "huige233.transcend.asm.ClassTransformer" };
    }

    public String getModContainerClass(){
        return null;
    }

    @Nullable
    public String getSetupClass(){
        return null;
    }

    public void injectData(Map<String, Object> data){
        RemapUtils.isDevelopmentEnvironment = !(boolean) data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass(){
        return null;
    }
}
