package net.toshayo.waterframes.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.ProjectorTileEntity;

public class ProjectorBlock extends DisplayBlock {
    public ProjectorBlock() {
        super();

        setRegistryName(new ResourceLocation(WaterFramesMod.MOD_ID, "projector"));
        setUnlocalizedName("projector");
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ProjectorTileEntity();
    }
}
