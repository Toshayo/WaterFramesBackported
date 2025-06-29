package net.toshayo.waterframes.client.render.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import org.lwjgl.opengl.GL11;

public class FrameRenderer extends DisplayRenderer {
    private static IBakedModel model = null;
    @Override
    public void render(DisplayTileEntity tileEntity, double x, double y, double z, float partialTicks, int p_192841_9_, float p_192841_10_) {
        if(model == null) {
            ModelResourceLocation modelLoc = new ModelResourceLocation(new ResourceLocation(WaterFramesMod.MOD_ID, "frame"), "normal");
            model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getModelManager()
                    .getModel(modelLoc);
        }

        if(tileEntity.isVisible()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            } else {
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            dispatcher.getBlockModelRenderer().renderModel(
                    tileEntity.getWorld(),
                    model,
                    tileEntity.getWorld().getBlockState(tileEntity.getPos()),
                    tileEntity.getPos(),
                    buffer,
                    false);
            tessellator.draw();

            GlStateManager.popMatrix();

            RenderHelper.enableStandardItemLighting();
        }

        super.render(tileEntity, x, y, z, partialTicks, p_192841_9_, p_192841_10_);
    }

    @Override
    public EnumFacing direction(DisplayTileEntity tile) {
        return EnumFacing.getFront(tile.getBlockMetadata()).getOpposite();
    }
}
