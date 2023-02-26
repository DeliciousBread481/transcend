package huige233.transcend.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import huige233.transcend.asm.transformers.ITransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassTransformer implements IClassTransformer {
    private List<ITransformer> targets = new ArrayList<>();

    public ClassTransformer() {
        ServiceLoader<ITransformer> sl = ServiceLoader.load(ITransformer.class, Launch.classLoader);
        for (ITransformer transformer : sl) {
            this.targets.add(transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (ITransformer transformer : this.targets) {
            if (transformedName.equals(transformer.targetClass())) {
                ClassNode cn = new ClassNode();
                new ClassReader(basicClass).accept(cn, ClassReader.EXPAND_FRAMES);
                cn = transformer.transform(cn);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                basicClass = cw.toByteArray();
            }
        }
        return basicClass;
    }
}
