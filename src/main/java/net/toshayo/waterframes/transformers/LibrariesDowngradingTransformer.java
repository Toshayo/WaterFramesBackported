package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.watermedia.loaders.ForgeLoader;

import java.util.function.Function;


public class LibrariesDowngradingTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (transformedName.equals("org.watermedia.api.render.RenderAPI")) {
            return visitClass(basicClass, classWriter -> new MemoryAllocVisitor(transformedName, classWriter));
        } else /*if (transformedName.startsWith("org.watermedia")) {
            //byte[] resultingClass = visitClass(basicClass, classWriter -> new Log4JDowngradingVisitor(transformedName, classWriter), false);
            if (transformedName.equals("org.watermedia.api.image.ImageFetch")) {
                resultingClass = visitClass(resultingClass, classWriter -> new ImageFetchCachingDisablingVisitor(transformedName, classWriter));
                //resultingClass = visitClass(resultingClass, classWriter -> new ImageFetchMimeGuessingVisitor(transformedName, classWriter));
            }
            return resultingClass;
        }*/ if (transformedName.equals("org.watermedia.api.image.ImageFetch")) {
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
