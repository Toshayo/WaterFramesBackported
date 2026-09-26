package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.*;

public class WATERMeDIALoadingPreventionTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.minecraftforge.fml.common.ModContainerFactory")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            WaterFramesPlugin.LOGGER.info("Disabling WATERMeDIA loading");

            classReader.accept(new ModContainerFactoryVisitor(classWriter), ClassReader.EXPAND_FRAMES);

            return classWriter.toByteArray();
        } else if(transformedName.equals("com.cleanroommc.discovery.IdentifiedMods")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            WaterFramesPlugin.LOGGER.info("Disabling WATERMeDIA loading in Cleanroom");

            classReader.accept(new IdentifiedModsVisitor(classWriter), ClassReader.EXPAND_FRAMES);

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
                            for(String className : new String[]{"org.watermedia.loaders.ForgeMCLoader", "org.watermedia.youtube.loaders.ForgeMCLoader"}) {
                                Label continueLabel = new Label();
                                mv.visitLdcInsn(className);
                                mv.visitVarInsn(Opcodes.ALOAD, 4);
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                                mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);

                                mv.visitInsn(Opcodes.ACONST_NULL);
                                mv.visitInsn(Opcodes.ARETURN);

                                mv.visitLabel(continueLabel);
                            }

                            injected = true;
                        }
                    }
                };
            }
            return methodVisitor;
        }
    }

    private static class IdentifiedModsVisitor extends ClassVisitor {
        public IdentifiedModsVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if(name.equals("<init>")) {
                MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                    @Override
                    public void visitCode() {
                        WaterFramesPlugin.LOGGER.info("Patching Cleanroom's IdentifiedMods");
                        super.visitCode();

                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitVarInsn(Opcodes.ALOAD, 2);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/PluginUtils", "onModList", "(Ljava/util/List;Ljava/util/List;)V", false);
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
