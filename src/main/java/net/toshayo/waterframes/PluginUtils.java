package net.toshayo.waterframes;

import com.sun.jna.Platform;
import me.eigenraven.lwjgl3ify.api.Lwjgl3Aware;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import net.toshayo.waterframes.utils.ExtensionsMimeTypes;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Lwjgl3Aware
public class PluginUtils {
    public static final String ARCH = System.getProperty("os.arch").toLowerCase().trim();
    private static final String OS = System.getProperty("os.name").toLowerCase().trim();

    public static boolean Platform_is64Bit() {
        return Platform.is64Bit();
    }

    public static boolean Platform_isARM() {
        return Platform.isARM();
    }

    public static boolean Platform_isWindows() {
        return Platform.isWindows();
    }

    public static boolean Platform_isMac() {
        return Platform.isMac();
    }

    public static boolean Platform_isLinux() {
        return Platform.isLinux();
    }

    public static ByteBuffer MemoryAlloc_createByteBuffer(int alignment, int size) {
        return MemoryUtil.memAlignedAlloc(alignment, size);
    }

    public static ByteBuffer MemoryAlloc_resizeByteBuffer(ByteBuffer buffer, int newSize) {
        MemoryUtil.MemoryAllocator allocator = MemoryUtil.getAllocator(false);
        long address = allocator.realloc(MemoryUtil.memAddress0((Buffer) buffer), newSize);
        if (address == 0L)
            throw new OutOfMemoryError("Insufficient memory to reallocate " + newSize + " bytes");

        return MemoryUtil.memByteBuffer(address, newSize);
    }

    public static void MemoryAlloc_freeByteBuffer(ByteBuffer buffer) {
        MemoryUtil.memAlignedFree(buffer);
    }

    public static String ImageFetch_getContentType(String type, URI uri) {
        if(type == null || type.equals("content/unknown")) {
            type = getMimeByExtension(FilenameUtils.getExtension(uri.getPath()));
        }
        return type;
    }

    private static String getMimeByExtension(String extension) {
        return ExtensionsMimeTypes.MIME_BY_EXTENSION.getOrDefault(extension, "content/unknown");
    }

    public static void deleteTextures(int[] textures) {
        IntBuffer buffer = BufferUtils.createIntBuffer(textures.length);
        buffer.put(textures).flip();
        GL11.glDeleteTextures(buffer);
    }


    private static long wf$lastWarnTime = 0;
    private static long wf$lastMillisTime = 0;
    private static long wf$timeStack = 0;
    public static long MinecraftServer_run_getSystemTimeMillis_0(final long v) {
        wf$lastMillisTime = v;
        return v;
    }

    public static long MinecraftServer_run_getSystemTimeMillis_1(final long millis) {
        if (!WFConfig.useLagTickCorrection()) {
            return millis;
        }
        long time = millis - wf$lastMillisTime;
        if (time > 100) // 50ms is 1 tick
            wf$timeStack += time;

        if (wf$timeStack > WaterFramesMod.SYNC_TIME) {
            DisplayTileEntity.setLagTickTime(wf$timeStack);
            if (millis - wf$lastWarnTime > 15000) {
                WaterFramesMod.LOGGER.warn("Server seems overloading, jumping {}ms or {} ticks", wf$timeStack, wf$timeStack / 50L);
                wf$lastWarnTime = millis;
            }
            wf$timeStack %= WaterFramesMod.SYNC_TIME;
        }

        wf$lastMillisTime = millis;
        return millis;
    }
}
