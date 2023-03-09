package huige233.transcend.asm.transformers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import huige233.transcend.asm.RemapUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityLivingBaseTransformer implements ITransformer {
    @Override
    public String targetClass() {
        return "net.minecraft.entity.EntityLivingBase";
    }

    @Override
    public ClassNode transform(ClassNode cn) {
        List<MethodNode> methods = new CopyOnWriteArrayList<>(cn.methods);
        for (MethodNode mn : methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_110143_aJ") && RemapUtils.checkMethodDesc(mn.desc, "()F")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "getHealth2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_110138_aP") && RemapUtils.checkMethodDesc(mn.desc, "()F")) {
                methods.add(new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0])));
                mn.name = "getMaxHealth2";
            }
        }
        cn.methods = methods;
        return cn;
    }
}
