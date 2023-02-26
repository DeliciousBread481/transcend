package huige233.transcend.asm.transformers;

import org.objectweb.asm.tree.ClassNode;

public interface ITransformer {
    String targetClass();

    ClassNode transform(ClassNode cn);
}
