package huige233.transcend.asm.transformers;

import java.util.Objects;

import huige233.transcend.asm.RemapUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ToolsTransformer implements ITransformer {
    @Override
    public String targetClass() {
        return "mrthomas20121.tinkers_reforged.tools.Tools";
    }

    @Override
    public ClassNode transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "registerItems") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraftforge/event/RegistryEvent$Register;)V")) {
                boolean isTarget = false;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (isTarget && ain.getOpcode() == Opcodes.INVOKESTATIC) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (RemapUtils.checkClassName(min.owner, "net/minecraftforge/fml/common/Loader") && RemapUtils.checkMethodName(min.owner, min.name, min.desc, "isModLoaded") && RemapUtils.checkMethodDesc(min.desc, "(Ljava/lang/String;)Z")) {
                            isTarget = false;
                            mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                            mn.instructions.set(ain, new InsnNode(Opcodes.ICONST_1));
                        }
                    } else if (ain.getOpcode() == Opcodes.LDC) {
                        LdcInsnNode lin = (LdcInsnNode) ain;
                        if (Objects.equals(lin.cst, "atum")) {
                            isTarget = true;
                        }
                    }
                }
            }
        }
        return cn;
    }
}
