package net.toshayo.waterframes.transformers;

import net.toshayo.waterframes.WaterFramesPlugin;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class Log4JDowngradingVisitor extends ClassVisitor {
    private final String transformedName;

    public Log4JDowngradingVisitor(String transformedName, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.transformedName = transformedName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                boolean isReplacableMethod = name.equals("info") || name.equals("warn") || name.equals("error") || name.equals("debug") || name.equals("fatal");
                if (opcode == Opcodes.INVOKEINTERFACE &&
                        owner.equals("org/apache/logging/log4j/Logger") && isReplacableMethod &&
                        desc.startsWith("(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;Ljava/lang/Object;")) {
                    WaterFramesPlugin.LOGGER.info("Patching {}.{} in {}.{}{}", "Logger", name, transformedName, methodName, desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/transformers/Log4JDowngradeHelper", name, "(Lorg/apache/logging/log4j/Logger;" + desc.substring(1), false);
                } else if (opcode == Opcodes.INVOKEINTERFACE &&
                        owner.equals("org/apache/logging/log4j/Logger") && isReplacableMethod &&
                        desc.startsWith("(Ljava/lang/String;Ljava/lang/Object;")) {
                    WaterFramesPlugin.LOGGER.info("Patching {}.{} in {}.{}{}", "Logger", name, transformedName, methodName, desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/toshayo/waterframes/transformers/Log4JDowngradeHelper", name, "(Lorg/apache/logging/log4j/Logger;" + desc.substring(1), false);
                } else {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            }
        };
    }
}
