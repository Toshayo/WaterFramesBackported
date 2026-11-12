package net.toshayo.waterframes.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.toshayo.waterframes.tileentities.*;
import org.lwjgl.opengl.GL11;

public class DisplayItemRenderer implements IItemRenderer {
    private final DisplayTileEntity dummy;

    public DisplayItemRenderer(Class<? extends DisplayTileEntity> displayClass) throws InstantiationException, IllegalAccessException {
        dummy = displayClass.newInstance();
    }
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        dummy.setWorldObj(Minecraft.getMinecraft().theWorld);
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F); // Adjust rendering position to center the block

        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0, 0.5F, 0);
                break;
            case EQUIPPED:
                GL11.glTranslatef(1.5F, 1.5F, 1F);
                GL11.glRotatef(220, 0, 1, 0);
                GL11.glRotatef(90, 1, 0, 0);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(2.5F, 0.2F, 1.5F);
                GL11.glRotatef(130, 0, 1, 0);
                break;
            case INVENTORY:
                GL11.glRotatef(180, 0, 1, 0);
                if(dummy instanceof TVTileEntity || dummy instanceof BigTVTileEntity) {
                    GL11.glTranslatef(0, -0.3F, 0);
                    GL11.glScalef(0.6F, 0.6F, 0.6F);
                }
                if(dummy instanceof TVTileEntity) {
                    GL11.glTranslatef(0, 0.5F, 0);
                }
                if(dummy instanceof FrameTileEntity) {
                    GL11.glTranslatef(0, 0.3F, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                }
                if(dummy instanceof TVBoxTileEntity) {
                    GL11.glTranslatef(0.2F, -0.2F, 0);
                }
                GL11.glTranslatef(-0.2F, -0.5F, 0);
                break;
            default:
                break;
        }

        TileEntityRendererDispatcher.instance.getSpecialRenderer(dummy)
                .renderTileEntityAt(dummy, 0.0D, 0.0D, 0.0D, 0.0F);

        GL11.glPopMatrix();
    }
}
