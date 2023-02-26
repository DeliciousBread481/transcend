package huige233.transcend.asm.transformers;

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
        for (MethodNode mn : cn.methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_70436_m") && RemapUtils.checkMethodDesc(mn.desc, "()V")) {
                mn.name = "dropAllItems2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_174925_a") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I")) {
                mn.name = "clearMatchingItems2";
            }
        }
        return cn;
    }
}
