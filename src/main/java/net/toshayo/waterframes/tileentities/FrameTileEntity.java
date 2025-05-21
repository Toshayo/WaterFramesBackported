package net.toshayo.waterframes.tileentities;

import net.toshayo.waterframes.DisplayCaps;
import net.toshayo.waterframes.DisplayData;
import net.toshayo.waterframes.blocks.FrameBlock;

public class FrameTileEntity extends DisplayTileEntity {
    public FrameTileEntity() {
        super(new DisplayData().setProjectionDistance(FrameBlock.THICKNESS), DisplayCaps.FRAME);
    }
}
