package net.toshayo.waterframes.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.*;

public class PatchJNISpamIssueTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("org.watermedia.videolan4j.support.eventmanager.NativeEventManager$EventCallback")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            classReader.accept(new EventCallbackVisitor(classWriter), ClassReader.EXPAND_FRAMES);

            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static class EventCallbackVisitor extends ClassVisitor {
        public EventCallbackVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if(name.equals("<init>")) {
                MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if(name.equals("setCallbackThreadInitializer")) {
                            WaterFramesPlugin.LOGGER.info("Removing Native.setCallbackThreadInitializer call in NativeEventManager$EventCallback");

                            mv.visitInsn(Opcodes.POP);
                            mv.visitInsn(Opcodes.POP);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        }
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
