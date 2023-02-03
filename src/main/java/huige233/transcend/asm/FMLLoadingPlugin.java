package huige233.transcend.asm;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
@Name("transcend")
public class FMLLoadingPlugin implements IFMLLoadingPlugin{
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
    }

    public String getAccessTransformerClass(){
        return null;
    }
}
