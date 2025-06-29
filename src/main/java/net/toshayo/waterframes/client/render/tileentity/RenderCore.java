package net.toshayo.waterframes.client.render.tileentity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.BoxCorner;
import toshayopack.team.creative.creativecore.common.util.math.BoxFace;

public class RenderCore {
    private static BufferBuilder buffer;

    public static void bufferBegin() {
        buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    public static void bufferEnd() {
        Tessellator.getInstance().draw();
        buffer = null;
    }

    public static void bindTex(int texture) {
        GlStateManager.bindTexture(texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static void vertexF(AlignedBox box, BoxFace face, boolean flipX, boolean flipY, int a, int r, int g, int b) {
        for (int i = 0; i < face.corners.length; i++) {
            vertex(box, face, face.corners[i], flipX, flipY, a, r, g, b);
        }
    }

    public static void vertexB(AlignedBox box, BoxFace face, boolean flipX, boolean flipY, int a, int r, int g, int b) {
        for (int i = face.corners.length - 1; i >= 0; i--) {
            vertex(box, face, face.corners[i], flipX, flipY, a, r, g, b);
        }
    }

    private static void vertex(AlignedBox box, BoxFace face, BoxCorner corner, boolean flipX, boolean flipY, int a, int r, int g, int b) {
        /*net.minecraft.util.math.Vec3d normal = face.facing.normal;
        buffer.normal((float) normal.x, (float) normal.y, (float) normal.z);*/
        if(buffer == null) {
            bufferBegin();
        }
        buffer.pos(box.get(corner.x), box.get(corner.y), box.get(corner.z))
                .tex(
                        corner.isFacing(face.getTexU()) != flipX ? 1 : 0,
                        corner.isFacing(face.getTexV()) != flipY ? 1 : 0
                )
                .color(r, g, b, a)
                .endVertex();
    }
}
