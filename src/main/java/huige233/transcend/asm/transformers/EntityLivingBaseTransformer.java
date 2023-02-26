package huige233.transcend.asm.transformers;

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
        for (MethodNode mn : cn.methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_110143_aJ") && RemapUtils.checkMethodDesc(mn.desc, "()F")) {
                mn.name = "getHealth2";
            } else if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_110138_aP") && RemapUtils.checkMethodDesc(mn.desc, "()F")) {
                mn.name = "getMaxHealth2";
            }
        }
        return cn;
    }
}
