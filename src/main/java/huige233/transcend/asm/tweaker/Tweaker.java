package huige233.transcend.asm.tweaker;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class Tweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.transcend.json");
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] getLaunchArguments() {
        try {
            Field transformersFiled = LaunchClassLoader.class.getDeclaredField("transformers");
            transformersFiled.setAccessible(true);
            List<IClassTransformer> transformers = (List<IClassTransformer>) transformersFiled.get(Launch.classLoader);
            for (int i = transformers.size() - 1; i >= 0; i--) {
//                if (transformers.get(i).getClass().getName().equals("com.anotherstar.core.transformer.LoliPickaxeTransformer")) {
//                    transformers.remove(i);
//                    break;
//                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return new String[0];
    }
}
