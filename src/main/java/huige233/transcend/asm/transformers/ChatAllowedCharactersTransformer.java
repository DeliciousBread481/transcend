package huige233.transcend.asm.transformers;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Map;

public class ChatAllowedCharactersTransformer implements ITransformer {

    @Override
    public String targetClass() {
        return "net.minecraft.util.ChatAllowedCharacters";
    }

    @Override
    public ClassNode transform(ClassNode cn) {

        for (MethodNode method : cn.methods) {
            String name = method.name;
            String desc = method.desc;
            if (name.equals("a") && desc.equals("(C)Z") || name.equals("isAllowedCharacter")) {
                Arrays.stream(method.instructions.toArray())
                        .filter(abstractInsnNode -> abstractInsnNode.getOpcode() == Opcodes.SIPUSH)
                        .findFirst().ifPresent(abstractInsnNode -> {
                            method.instructions.set(abstractInsnNode, new AbstractInsnNode(abstractInsnNode.getOpcode()) {
                                @Override
                                public int getType() {
                                    return abstractInsnNode.getType();
                                }

                                @Override
                                public void accept(MethodVisitor cv) {
                                    cv.visitIntInsn(Opcodes.SIPUSH, 127);
                                }

                                @Override
                                public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
                                    return abstractInsnNode.clone(labels);
                                }
                            });
                        });
            }
        }
        return cn;
    }
}
