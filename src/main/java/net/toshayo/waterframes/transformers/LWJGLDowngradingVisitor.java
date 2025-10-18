package net.toshayo.waterframes.transformers;

import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LWJGLDowngradingVisitor  extends ClassVisitor {
    private final String transformedName;

    public LWJGLDowngradingVisitor(String transformedName, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.transformedName = transformedName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        if (methodName.equals("deleteTexture") && desc.equals("([I)V")) {
            MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitCode() {
                    WaterFramesPlugin.LOGGER.info("Patching {}.{}{}", transformedName, methodName, desc);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/PluginUtils", "RenderAPI_deleteTextures", "([I)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                }
            };
        }
        return super.visitMethod(access, methodName, desc, signature, exceptions);
    }
}
