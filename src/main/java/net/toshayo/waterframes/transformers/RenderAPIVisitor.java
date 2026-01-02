package net.toshayo.waterframes.transformers;

import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.nio.IntBuffer;


public class RenderAPIVisitor extends ClassVisitor {
    private final String transformedName;

    public RenderAPIVisitor(String transformedName, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.transformedName = transformedName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        if ((methodName.equals("createByteBuffer") && desc.startsWith("(II)")) || methodName.equals("resizeByteBuffer") || methodName.equals("freeByteBuffer")) {
            MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitCode() {
                    WaterFramesPlugin.LOGGER.info("Patching {}.{}{} to use LWJGL3ify compatible calls", transformedName, methodName, desc);
                    switch (methodName) {
                        case "createByteBuffer":
                            mv.visitVarInsn(Opcodes.ILOAD, 0);
                            mv.visitVarInsn(Opcodes.ILOAD, 1);
                            break;
                        case "resizeByteBuffer":
                            mv.visitVarInsn(Opcodes.ALOAD, 0);
                            mv.visitVarInsn(Opcodes.ILOAD, 1);
                            break;
                        case "freeByteBuffer":
                            mv.visitVarInsn(Opcodes.ALOAD, 0);
                            break;
                    }
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/PluginUtils", "MemoryAlloc_" + methodName, desc, false);
                    if (methodName.equals("freeByteBuffer")) {
                        mv.visitInsn(Opcodes.RETURN);
                    } else {
                        mv.visitInsn(Opcodes.ARETURN);
                    }
                }
            };
        } else if(methodName.equals("deleteTexture") && desc.equals("([I)V")) {
            MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitCode() {
                    WaterFramesPlugin.LOGGER.info("Patching {}.{}{} to use correct method signature", transformedName, methodName, desc);

                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/nio/IntBuffer", "wrap", "([I)Ljava/nio/IntBuffer;", false);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDeleteTextures", "(Ljava/nio/IntBuffer;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                }
            };
        }
        return super.visitMethod(access, methodName, desc, signature, exceptions);
    }
}
