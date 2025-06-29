package net.toshayo.waterframes.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.toshayo.waterframes.blocks.ExtendedBlockStateProvider;
import net.toshayo.waterframes.tileentities.FrameTileEntity;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class FrameBakedModel implements IBakedModel {
    private final IBakedModel original;

    public FrameBakedModel(IBakedModel original) {
        this.original = original;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState iBlockState, @Nullable EnumFacing enumFacing, long l) {
        if(iBlockState instanceof IExtendedBlockState) {
            TileEntity tile = ((IExtendedBlockState) iBlockState).getValue(ExtendedBlockStateProvider.TILE);
            if(tile instanceof FrameTileEntity) {
                if(!((FrameTileEntity) tile).isVisible()) {
                    return Collections.emptyList();
                }
            }
        }
        return original.getQuads(iBlockState, enumFacing, l);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return original.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return original.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return original.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return original.getOverrides();
    }
}
