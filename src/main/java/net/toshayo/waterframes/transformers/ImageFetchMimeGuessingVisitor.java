package net.toshayo.waterframes.transformers;

import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ImageFetchMimeGuessingVisitor extends ClassVisitor {
    private final String transformedName;

    public ImageFetchMimeGuessingVisitor(String transformedName, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.transformedName = transformedName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
        if (methodName.equals("run") && desc.equals("()V")) {
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/URLConnection") && name.equals("getContentType")) {
                        WaterFramesPlugin.LOGGER.info("Patching {}.{} in {}.{}{}", owner, name, transformedName, methodName, desc);
                        mv.visitVarInsn(Opcodes.ALOAD, 4);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/PluginUtils", "ImageFetch_getContentType", "(Ljava/lang/String;Ljava/net/URI;)Ljava/lang/String;", false);
                    }
                }
            };
        }
        return methodVisitor;
    }
}
