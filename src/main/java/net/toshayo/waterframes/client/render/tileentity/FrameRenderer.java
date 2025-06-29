package net.toshayo.waterframes.client.render.tileentity;

import net.minecraft.util.EnumFacing;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public class FrameRenderer extends DisplayRenderer {
    @Override
    public EnumFacing direction(DisplayTileEntity tile) {
        return EnumFacing.getFront(tile.getBlockMetadata()).getOpposite();
    }
}
