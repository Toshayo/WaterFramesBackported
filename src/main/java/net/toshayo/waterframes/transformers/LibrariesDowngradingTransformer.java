package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.function.Function;


public class LibrariesDowngradingTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (transformedName.equals("org.watermedia.api.render.RenderAPI")) {
            basicClass = visitClass(basicClass, classWriter -> new MemoryAllocVisitor(transformedName, classWriter));
            return visitClass(basicClass, classWriter -> new LWJGLDowngradingVisitor(transformedName, classWriter));
        } else if (transformedName.equals("org.watermedia.api.image.ImageFetch")) {
            return visitClass(basicClass, classWriter -> new ImageFetchCachingDisablingVisitor(transformedName, classWriter), false);
        }
        return basicClass;
    }

    private byte[] visitClass(byte[] basicClass, Function<ClassWriter, ClassVisitor> visitor) {
        return visitClass(basicClass, visitor, true);
    }

    private byte[] visitClass(byte[] basicClass, Function<ClassWriter, ClassVisitor> visitor, boolean handleFrames) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(handleFrames ? ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES : 0);

        classReader.accept(visitor.apply(classWriter), handleFrames ? ClassReader.EXPAND_FRAMES : 0);

        return classWriter.toByteArray();
    }
}
