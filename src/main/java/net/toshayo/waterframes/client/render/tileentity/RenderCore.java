package net.toshayo.waterframes.client.render.tileentity;

import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.BoxCorner;
import toshayopack.team.creative.creativecore.common.util.math.BoxFace;

public class RenderCore {
    private static final Tessellator tesselator = Tessellator.instance;

    public static void bufferBegin() {
        tesselator.startDrawingQuads();
    }

    public static void bufferEnd() {
        tesselator.draw();
    }

    public static void bindTex(int texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
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
        /*Vec3 normal = face.facing.normal;
        tesselator.setNormal((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);
        tesselator.setColorRGBA(r, g, b, a);*/
        tesselator.addVertexWithUV(
                box.get(corner.x),
                box.get(corner.y),
                box.get(corner.z),
                corner.isFacing(face.getTexU()) != flipX ? 1 : 0,
                corner.isFacing(face.getTexV()) != flipY ? 1 : 0
        );
    }
}
