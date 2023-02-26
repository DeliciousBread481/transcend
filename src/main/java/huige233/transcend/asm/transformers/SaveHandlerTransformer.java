package huige233.transcend.asm.transformers;

import huige233.transcend.asm.RemapUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class SaveHandlerTransformer implements ITransformer {
    @Override
    public String targetClass() {
        return "net.minecraft.world.storage.SaveHandler";
    }

    @Override
    public ClassNode transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_75752_b") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/nbt/NBTTagCompound;")) {
                mn.name = "readPlayerData2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_75753_a") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/entity/player/EntityPlayer;)V")) {
                mn.name = "writePlayerData2";
            }
        }
        return cn;
    }
}
