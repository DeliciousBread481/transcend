package huige233.transcend.asm.transformers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        List<MethodNode> methods = new CopyOnWriteArrayList<>(cn.methods);
        for (MethodNode mn : methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_75752_b") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/nbt/NBTTagCompound;")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "readPlayerData2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_75753_a") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/entity/player/EntityPlayer;)V")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "writePlayerData2";
            }
        }
        cn.methods = methods;
        return cn;
    }
}
