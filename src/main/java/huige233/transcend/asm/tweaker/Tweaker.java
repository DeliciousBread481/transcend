package huige233.transcend.asm.tweaker;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.List;

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
    public String[] getLaunchArguments() {
        try {
            if (Launch.classLoader instanceof LaunchClassLoader) {
                LaunchClassLoader lc = (LaunchClassLoader) Launch.classLoader;
            }
        } catch (Throwable t) {
            System.out.println("[Tweaker] transformers field not accessible, skipping removal.");
        }
        return new String[0];
    }
}


//if (transformers.get(i).getClass().getName().equals("com.anotherstar.core.transformer.LoliPickaxeTransformer")) {
//                    transformers.remove(i);
//                    break;
//                }