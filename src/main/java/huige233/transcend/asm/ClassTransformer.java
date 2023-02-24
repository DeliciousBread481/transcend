package huige233.transcend.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;

public class ClassTransformer implements IClassTransformer {
    private boolean inTransform = false;
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.minecraftforge.common.ForgeHooks")) {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {

                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("onLivingDeath")) {
                        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                        mv.visitCode();
                        Label start = new Label();
                        mv.visitLabel(start);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "onLivingDeath", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;)Z", false);
                        mv.visitInsn(Opcodes.IRETURN);
                        Label end = new Label();
                        mv.visitLabel(end);
                        mv.visitLocalVariable("entity", "Lnet/minecraft/entity/EntityLivingBase;", null, start, end, 0);
                        mv.visitLocalVariable("src", "Lnet/minecraft/util/DamageSource;", null, start, end, 1);
                        mv.visitMaxs(2, 2);
                        mv.visitEnd();
                        return null;
                    } else if (name.equals("onLivingUpdate")) {
                        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                        mv.visitCode();
                        Label start = new Label();
                        mv.visitLabel(start);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "onLivingUpdate", "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                        Label end = new Label();
                        mv.visitLabel(end);
                        mv.visitLocalVariable("entity", "Lnet/minecraft/entity/EntityLivingBase;", null, start, end, 0);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return null;
                    }
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }
            };
            reader.accept(visitor, Opcodes.ASM4);
            return writer.toByteArray();
        }else if(transformedName.equals("net.minecraft.entity.EntityLivingBase")){
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {
                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    if(name.equals("aT") || name.equals("aE")){
                        access = Opcodes.ACC_PUBLIC;
                    }
                    return cv.visitField(access, name, desc, signature, value);
                }
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC, "transcendDead", "Z", null, 0);
                    fv.visitEnd();
                    fv = cv.visitField(Opcodes.ACC_PUBLIC, "transcendDeadTime", "I", null, 0);
                    fv.visitEnd();
                    fv = cv.visitField(Opcodes.ACC_PUBLIC, "transcendCool", "Z", null, 0);
                    fv.visitEnd();
                    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "getHealth", "()F", null, null);
                    mv.visitCode();
                    Label start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "getHealth", "(Lnet/minecraft/entity/EntityLivingBase;)F", false);
                    mv.visitInsn(Opcodes.FRETURN);
                    Label end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/entity/EntityLivingBase;", null, start, end, 0);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                    mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "getHealth", "()F", null, null);
                    mv.visitCode();
                    start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "getHealth", "(Lnet/minecraft/entity/EntityLivingBase;)F", false);
                    mv.visitInsn(Opcodes.FRETURN);
                    end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/entity/EntityLivingBase;", null, start, end, 0);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                }
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
                    if(name.equals("getHealth")){
                        return cv.visitMethod(access, "getHealth2", desc,signature,exceptions);
                    } else if (name.equals("getMaxHealth")) {
                        return cv.visitMethod(access, "getMaxHealth2", desc,signature,exceptions);
                    } else if (name.equals("getHealth2") || name.equals("getMaxHealth2")) {
                        return new MethodVisitor(Opcodes.ASM4, cv.visitMethod(access,name,desc,signature,exceptions)){
                            public void visitInsn(int opcode){
                                if(opcode == Opcodes.FRETURN){
                                    super.visitVarInsn(Opcodes.ALOAD, 0);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "onUpdate", "(Lnet/minecraft/entity/EntityLivingBase;)F", false);
                                }
                                mv.visitInsn(opcode);
                            };
                        };
                    };
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }
            };
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }else if(transformedName.equals("net.minecraft.world.storage.SaveHandler")){
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,"readPlayerData","(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/nbt/NBTTagCompound;", null, null);
                    mv.visitCode();
                    Label start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "readPlayerData", "(Lnet/minecraft/world/storage/SaveHandler;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/nbt/NBTTagCompound;", false);
                    mv.visitInsn(Opcodes.ARETURN);
                    Label end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/world/storage/SaveHandler;", null, start, end, 0);
                    mv.visitLocalVariable("player", "Lnet/minecraft/entity/player/EntityPlayer;", null, start, end, 1);
                    mv.visitMaxs(2, 2);
                    mv.visitEnd();
                    mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "writePlayerData","(Lnet/minecraft/entity/player/EntityPlayer;)V", null, null);
                    mv.visitCode();
                    start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "writePlayerData", "(Lnet/minecraft/world/storage/SaveHandler;Lnet/minecraft/entity/player/EntityPlayer;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                    end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/world/storage/SaveHandler;", null, start, end, 0);
                    mv.visitLocalVariable("player", "Lnet/minecraft/entity/player/EntityPlayer;", null, start, end, 1);
                    mv.visitMaxs(2, 2);
                    mv.visitEnd();
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("b") && desc.equals("(Laed;)Lfy;") || name.equals("readPlayerData")) {
                        return cv.visitMethod(access, "readPlayerData2", desc, signature, exceptions);
                    } else if (name.equals("a") && desc.equals("(Laed;)V") || name.equals("writePlayerData")) {
                        return cv.visitMethod(access, "writePlayerData2", desc, signature, exceptions);
                    } else if (name.equals("readPlayerData2") || name.equals("writePlayerData2")) {
                        return null;
                    }
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }

            };
            classReader.accept(classVisitor, Opcodes.ASM4);
            return classWriter.toByteArray();
        } else if (transformedName.equals("net.minecraft.entity.player.InventoryPlayer")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,"dropAllItems", "()V", null, null);
                    mv.visitCode();
                    Label start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "dropAllItems", "(Lnet/minecraft/entity/player/InventoryPlayer;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                    Label end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/entity/player/InventoryPlayer;", null, start, end, 0);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                    mv = cv.visitMethod(Opcodes.ACC_PUBLIC,"clearMatchingItems","(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I", null, null);
                    mv.visitCode();
                    start = new Label();
                    mv.visitLabel(start);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitVarInsn(Opcodes.ILOAD, 2);
                    mv.visitVarInsn(Opcodes.ILOAD, 3);
                    mv.visitVarInsn(Opcodes.ALOAD, 4);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "clearMatchingItems", "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I", false);
                    mv.visitInsn(Opcodes.IRETURN);
                    end = new Label();
                    mv.visitLabel(end);
                    mv.visitLocalVariable("this", "Lnet/minecraft/entity/player/InventoryPlayer;", null, start, end, 0);
                    mv.visitLocalVariable("item", "Lnet/minecraft/item/Item;", null, start, end, 1);
                    mv.visitLocalVariable("metadata", "I", null, start, end, 2);
                    mv.visitLocalVariable("removeCount", "I", null, start, end, 3);
                    mv.visitLocalVariable("itemNBT", "Lnet/minecraft/nbt/NBTTagCompound;", null, start, end, 4);
                    mv.visitMaxs(5, 5);
                    mv.visitEnd();
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("o") && desc.equals("()V") || name.equals("dropAllItems")) {
                        return cv.visitMethod(access, "dropAllItems2", desc, signature, exceptions);
                    } else if (name.equals("a") && desc.equals("(Lain;IILfy;)I") || name.equals("clearMatchingItems")) {
                        return cv.visitMethod(access, "clearMatchingItems2", desc, signature, exceptions);
                    } else if (name.equals("dropAllItems2") || name.equals("clearMatchingItems2")) {
                        return null;
                    }
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }

            };
            classReader.accept(classVisitor, Opcodes.ASM4);
            return classWriter.toByteArray();
        } else if (transformedName.equals("net.minecraft.entity.Entity")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    if (name.equals("aF")) {
                        access = Opcodes.ACC_PUBLIC;
                    }
                    return cv.visitField(access, name, desc, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("rayTrace")) {
                        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                        mv.visitCode();
                        Label start = new Label();
                        mv.visitLabel(start);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitVarInsn(Opcodes.DLOAD, 1);
                        mv.visitVarInsn(Opcodes.FLOAD, 3);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "huige233/transcend/util/EventUtil", "rayTrace", "(Lnet/minecraft/entity/Entity;DF)Lnet/minecraft/util/math/RayTraceResult;", false);
                        mv.visitInsn(Opcodes.ARETURN);
                        Label end = new Label();
                        mv.visitLabel(end);
                        mv.visitLocalVariable("this", "Lnet/minecraft/entity/Entity;", null, start, end, 0);
                        mv.visitLocalVariable("blockReachDistance", "D", null, start, end, 1);
                        mv.visitLocalVariable("partialTicks", "F", null, start, end, 3);
                        mv.visitMaxs(4, 4);
                        mv.visitEnd();
                        return null;
                    }
                    return cv.visitMethod(access, name, desc, signature, exceptions);
                }

            };
            classReader.accept(classVisitor, Opcodes.ASM4);
            return classWriter.toByteArray();
        } else if (transformedName.equals("net.minecraft.util.ChatAllowedCharacters")) {
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
        }
        return basicClass;
    }
}