package net.toshayo.waterframes;

import com.sun.jna.Platform;
import me.eigenraven.lwjgl3ify.api.Lwjgl3Aware;
import net.toshayo.waterframes.utils.ExtensionsMimeTypes;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.system.MemoryUtil;

import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;

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
}
