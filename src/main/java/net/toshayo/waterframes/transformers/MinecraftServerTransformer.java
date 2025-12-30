package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.*;

public class MinecraftServerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (transformedName.equals("net.minecraft.server.MinecraftServer")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(0);

            classReader.accept(new MinecraftServerVisitor(transformedName, classWriter), 0);

            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static class MinecraftServerVisitor extends ClassVisitor {
        private final String transformedName;

        private int index = 0;

        protected MinecraftServerVisitor(String transformedName, ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor);
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
                        if (index < 2 && opcode == Opcodes.INVOKESTATIC && owner.equals("net/minecraft/server/MinecraftServer") && (name.equals("getSystemTimeMillis") || name.equals("ar")) && desc.equals("()J")) {
                            WaterFramesPlugin.LOGGER.info("Patching {}.{} in {}.{}{} index {}", owner, name, transformedName, methodName, desc, index);
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/PluginUtils", "MinecraftServer_run_getSystemTimeMillis_" + index++, "(J)J", false);
                        }
                    }
                };
            }
            return methodVisitor;
        }
    }
}
