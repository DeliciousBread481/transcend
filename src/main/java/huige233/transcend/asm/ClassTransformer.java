package huige233.transcend.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class ClassTransformer implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.util.ChatAllowedCharacters")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("a") && desc.equals("(C)Z") || name.equals("isAllowedCharacter")) {
                        return new MethodVisitor(Opcodes.ASM5, cv.visitMethod(access, name, desc, signature, exceptions)) {
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.SIPUSH && operand == 167) {
                                    operand = 127;
                                }
                                super.visitIntInsn(opcode, operand);
                            };
                        };
                    }
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }
            };
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } else if ("mrthomas20121.tinkers_reforged.tools.Tools".equals(transformedName)) {
            ClassWriter cw = new ClassWriter(1);
            (new ClassReader(basicClass)).accept(new ClassVisitor(327680, (ClassVisitor)cw) {
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    if ("registerItems".equals(name) && "(Lnet/minecraftforge/event/RegistryEvent$Register;)V".equals(desc))
                        return new MethodVisitor(327680, mv) {
                            private boolean isTarget = false;

                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if (this.isTarget && 184 == opcode && "net/minecraftforge/fml/common/Loader".equals(owner) && "isModLoaded".equals(name) && "(Ljava/lang/String;)Z".equals(desc)) {
                                    this.isTarget = false;
                                    visitInsn(87);
                                    visitInsn(4);
                                } else {
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                }
                            }

                            public void visitLdcInsn(Object cst) {
                                if ("atum".equals(cst))
                                    this.isTarget = true;
                                super.visitLdcInsn(cst);
                            }
                        };
                    return mv;
                }
            }, 8);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}