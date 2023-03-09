package huige233.transcend.asm.transformers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import huige233.transcend.asm.RemapUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class InventoryPlayerTransformer implements ITransformer {
    @Override
    public String targetClass() {
        return "net.minecraft.entity.player.InventoryPlayer";
    }

    @Override
    public ClassNode transform(ClassNode cn) {
        List<MethodNode> methods = new CopyOnWriteArrayList<>(cn.methods);
        for (MethodNode mn : methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_70436_m") && RemapUtils.checkMethodDesc(mn.desc, "()V")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "dropAllItems2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_174925_a") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "clearMatchingItems2";
            }
        }
        cn.methods = methods;
        return cn;
    }
}
