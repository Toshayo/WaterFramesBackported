package net.toshayo.waterframes.transformers;

import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class ImageFetchCachingDisablingVisitor extends ClassVisitor {
    private final String transformedName;

    public ImageFetchCachingDisablingVisitor(String transformedName, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.transformedName = transformedName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
        if (methodName.equals("openConnection") && desc.equals("(Ljava/net/URI;Lorg/watermedia/api/cache/CacheAPI$Entry;)Ljava/net/URLConnection;")) {
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/URLConnection") && name.equals("setDefaultUseCaches")) {
                        WaterFramesPlugin.LOGGER.info("Patching {}.{} in {}.{}{}", owner, name, transformedName, methodName, desc);
                        mv.visitInsn(Opcodes.POP);
                        mv.visitInsn(Opcodes.ICONST_1);
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }
        return methodVisitor;
    }
}
