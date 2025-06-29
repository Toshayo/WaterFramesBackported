package net.toshayo.waterframes.client.render.tileentity;

import com.hbm.render.amlfrom1710.AdvancedModelLoader;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.util.ResourceLocation;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public class BigTVRenderer extends DisplayRenderer {
    private static final ResourceLocation BIG_TV_TEXTURE = new ResourceLocation(WaterFramesMod.MOD_ID, "textures/block/big_tv.png");
    private static final IModelCustom BIG_TV_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/big_tv.obj"));


    @Override
    public void render(DisplayTileEntity tile, double x, double y, double z, float partialTicks, int p_192841_9_, float p_192841_10_) {
        renderModel(BIG_TV_TEXTURE, BIG_TV_MODEL, x, y, z, tile.blockFacing, false);
        super.render(tile, x, y, z, partialTicks, p_192841_9_, p_192841_10_);
    }
}
