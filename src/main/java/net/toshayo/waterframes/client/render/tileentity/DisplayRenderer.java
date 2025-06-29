package net.toshayo.waterframes.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.client.DisplayControl;
import net.toshayo.waterframes.client.TextureDisplay;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import org.lwjgl.opengl.GL11;
import org.watermedia.api.image.ImageAPI;
import org.watermedia.api.image.ImageRenderer;
import org.watermedia.api.math.MathAPI;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.Axis;
import toshayopack.team.creative.creativecore.common.util.math.BoxFace;
import toshayopack.team.creative.creativecore.common.util.math.Facing;

public class DisplayRenderer extends TileEntitySpecialRenderer<DisplayTileEntity> {
    public static final ImageRenderer LOADING_TEX = ImageAPI.loadingGif();

    @Override
    public void render(DisplayTileEntity tile, double x, double y, double z, float partialTicks, int p_192841_9_, float p_192841_10_) {
        TextureDisplay display = tile.requestDisplay();
        if (display == null || !WFConfig.keepsRendering()) {
            return;
        }

        display.preRender();

        // PREPARE RENDERING
        GL11.glPushMatrix();
        //boolean blendBackup = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthTestBackup = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        //int srcAlphaBackup = GL11.glGetInteger(GL11.GL_SRC_ALPHA);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // variables
        EnumFacing direction = this.direction(tile);
        Facing facing = Facing.get(direction);
        AlignedBox box = new AlignedBox(tile.getRenderBox());


        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glRotatef(-tile.data.rotation, direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
        if(tile.caps.invertedFace(tile)) {
            GL11.glRotatef(-180, 0, 1, 0);
        }
        GL11.glTranslated(-0.5, -0.5, -0.5);

        if (direction == EnumFacing.UP || direction == EnumFacing.SOUTH || direction == EnumFacing.EAST) {
            if (!tile.caps.invertedFace(tile)) {
                box.setMax(facing.axis, box.getMax(facing.axis) + tile.caps.growSize());
            } else {
                box.setMin(facing.axis, box.getMin(facing.axis) - tile.caps.growSize());
            }
        } else {
            if (!tile.caps.invertedFace(tile)) {
                box.setMin(facing.axis, box.getMin(facing.axis) - tile.caps.growSize());
            } else {
                box.setMax(facing.axis, box.getMax(facing.axis) + tile.caps.growSize());
            }
        }

        // RENDERING
        final int brightness = tile.data.brightness;
        this.renderI(tile, display, box, BoxFace.get(tile.caps.invertedFace(tile) ? facing.opposite() : facing), tile.data.alpha, brightness, brightness, brightness);

        // POST RENDERING
        /*if(blendBackup) {
            GL11.glDisable(GL11.GL_BLEND);
        }*/
        if(!depthTestBackup) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        /*if(GL11.glGetInteger(GL11.GL_SRC_ALPHA) != srcAlphaBackup) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, srcAlphaBackup);
        }*/
        GL11.glPopMatrix();
    }

    public void renderI(DisplayTileEntity tile, TextureDisplay display, AlignedBox box, BoxFace face, int a, int r, int g, int b) {
        // VAR DECLARE
        final boolean flipX = this.flipX(tile);
        final boolean flipY = this.flipY(tile);
        final boolean front = this.inFront(tile);
        final boolean back = this.inBack(tile);

        if (display.isLoading()) {
            RenderCore.bufferBegin();
            this.renderLoading(tile, box, face, front, back, flipX, flipY);
            RenderCore.bufferEnd();
            return;
        }

        if (!display.canRender()) {
            return;
        }

        int tex = display.getTextureId();
        if (tex != -1) {
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);
            RenderCore.bufferBegin();
            RenderCore.bindTex(tex);
            if (front) {
                RenderCore.vertexF(box, face, flipX, flipY, a, r, g, b);
            }

            if (back) {
                RenderCore.vertexB(box, face, flipX, flipY, a, r, g, b);
            }
            RenderCore.bufferEnd();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        if (display.isBuffering()) {
            RenderCore.bufferBegin();
            this.renderLoading(tile, box, face, front, back, flipX, flipY);
            RenderCore.bufferEnd();
        }
    }

    public void renderLoading(DisplayTileEntity tile, AlignedBox alignedBox, BoxFace face, boolean front, boolean back, boolean flipX, boolean flipY) {
        RenderCore.bindTex(LOADING_TEX.texture(DisplayControl.getTicks(), MathAPI.tickToMs(WaterFramesMod.proxy.deltaFrames()), true));

        AlignedBox box = new AlignedBox(alignedBox);
        Facing facing = face.getFacing();

        Axis one = facing.one();
        Axis two = facing.two();

        float width = box.getSize(one);
        float height = box.getSize(two);
        if (width <= height) {
            width = box.getSize(two);
            height = box.getSize(one);
        }
        float subtracts = ((width - height) / 2f);
        float marginSubstract = height / 4;
        box.setMin(one, (box.getMin(one) + subtracts) + marginSubstract);
        box.setMax(one, (box.getMax(one) - subtracts) - marginSubstract);
        box.setMin(two, box.getMin(two) + marginSubstract);
        box.setMax(two, box.getMax(two) - marginSubstract);

        if (facing.positive) {
            box.setMax(face.getFacing().axis, alignedBox.getMax(facing.axis) + (tile.caps.projects() ? -0.001f : 0.001f));
        } else {
            box.setMin(facing.axis, alignedBox.getMin(facing.axis) - (tile.caps.projects() ? -0.001f : 0.001f));
        }

        if (front)
            RenderCore.vertexF(box, face, flipX, flipY, 255, 255, 255, 255);

        if (back)
            RenderCore.vertexB(box, face, flipX, flipY, 255, 255, 255, 255);
    }

    public boolean inFront(DisplayTileEntity tile) {
        return !tile.caps.projects() || tile.data.renderBothSides;
    }

    public boolean inBack(DisplayTileEntity tile) {
        return tile.caps.projects() || tile.data.renderBothSides;
    }

    public boolean flipX(DisplayTileEntity tile) {
        return tile.caps.projects() != tile.data.flipX;
    }

    public boolean flipY(DisplayTileEntity tile) {
        return tile.data.flipY;
    }

    public EnumFacing direction(DisplayTileEntity tile) {
        return tile.blockFacing;
    }
}
