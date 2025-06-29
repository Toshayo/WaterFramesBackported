package net.toshayo.waterframes.client.render.tileentity;

import com.hbm.render.amlfrom1710.AdvancedModelLoader;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public class TVRenderer extends DisplayRenderer {
    private static final ResourceLocation TV_TEXTURE = new ResourceLocation(WaterFramesMod.MOD_ID, "textures/block/tv.png");
    private static final IModelCustom TV_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv.obj"));
    private static final IModelCustom TV_WALL_TOP_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_wall_top.obj"));
    private static final IModelCustom TV_WALL_TOP_RECLINED_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_wall_top_reclined.obj"));
    private static final IModelCustom TV_BACK_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_back.obj"));
    private static final IModelCustom TV_BACK_EXTENDED_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_back_extended.obj"));
    private static final IModelCustom TV_WALL_LEFT_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_wall_left.obj"));
    private static final IModelCustom TV_WALL_RIGHT_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(WaterFramesMod.MOD_ID, "models/tv_wall_right.obj"));


    @Override
    public void render(DisplayTileEntity tileEntity, double x, double y, double z, float partialTicks, int p_192841_9_, float p_192841_10_) {
        IModelCustom model = TV_MODEL;
        EnumFacing attachedFace = tileEntity.getAttachedFace();
        EnumFacing blockFacing = tileEntity.blockFacing;

        if(blockFacing == attachedFace) {
            model = TV_BACK_EXTENDED_MODEL;
        } else if(blockFacing == attachedFace.getOpposite()) {
            model = TV_BACK_MODEL;
        } else if(attachedFace == EnumFacing.DOWN) {
            model = TV_WALL_TOP_MODEL;
        } else if(attachedFace != EnumFacing.UP){
            switch (blockFacing) {
                case NORTH:
                    if(attachedFace == EnumFacing.EAST) {
                        model = TV_WALL_RIGHT_MODEL;
                    } else {
                        model = TV_WALL_LEFT_MODEL;
                    }
                    break;
                case EAST:
                    if(attachedFace == EnumFacing.SOUTH) {
                        model = TV_WALL_RIGHT_MODEL;
                    } else {
                        model = TV_WALL_LEFT_MODEL;
                    }
                    break;
                case SOUTH:
                    if(attachedFace == EnumFacing.WEST) {
                        model = TV_WALL_RIGHT_MODEL;
                    } else {
                        model = TV_WALL_LEFT_MODEL;
                    }
                    break;
                case WEST:
                    if(attachedFace == EnumFacing.NORTH) {
                        model = TV_WALL_RIGHT_MODEL;
                    } else {
                        model = TV_WALL_LEFT_MODEL;
                    }
                    break;
            }
        }

        renderModel(TV_TEXTURE, model, x, y, z, blockFacing, false);

        super.render(tileEntity, x, y, z, partialTicks, p_192841_9_, p_192841_10_);
    }
}
