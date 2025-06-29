package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class WATERMeDIALoadingPreventionTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.minecraftforge.fml.common.ModContainerFactory")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            classReader.accept(new ModContainerFactoryVisitor(classWriter), ClassReader.EXPAND_FRAMES);

            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static class ModContainerFactoryVisitor extends ClassVisitor {
        public ModContainerFactoryVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("build")) {
                return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                    private boolean injected = false;

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        if(!injected && owner.equals("org/apache/logging/log4j/Logger") && name.equals("debug")) {
                            Label continueLabel = new Label();
                            mv.visitLdcInsn("org.watermedia.loaders.ForgeLoader");
                            mv.visitVarInsn(Opcodes.ALOAD, 4);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                            mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);

                            mv.visitInsn(Opcodes.ACONST_NULL);
                            mv.visitInsn(Opcodes.ARETURN);

                            mv.visitLabel(continueLabel);

                            injected = true;
                        }
                    }
                };
            }
            return methodVisitor;
        }
    }
}
