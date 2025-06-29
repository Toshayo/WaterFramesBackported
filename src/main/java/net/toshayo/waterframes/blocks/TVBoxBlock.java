package net.toshayo.waterframes.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.TVBoxTileEntity;
import net.toshayo.waterframes.tileentities.TVTileEntity;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.Axis;
import toshayopack.team.creative.creativecore.common.util.math.Facing;

public class TVBoxBlock extends DisplayBlock {
    private static final AlignedBox STATIC_BOX = new AlignedBox();

    public TVBoxBlock() {
        super();

        setRegistryName(new ResourceLocation(WaterFramesMod.MOD_ID, "tv_box"));
        setUnlocalizedName("tv_box");
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TVBoxTileEntity();
    }

    public static AlignedBox box(EnumFacing direction, boolean renderMode) {
        if (!renderMode) return STATIC_BOX;

        Facing facing = Facing.get(direction.getOpposite());
        AlignedBox box = new AlignedBox();

        // fit
        if (facing.positive) {
            box.setMin(facing.axis, (1f / 16f));
        } else {
            box.setMax(facing.axis, (15f / 16f));
        }

        Axis one = facing.one();
        Axis two = facing.two();

        if (facing.axis != Axis.Z) {
            one = facing.two();
            two = facing.one();
        }

        // fit height
        box.setMin(two, 6f / 16f);
        box.setMax(two, 14f / 16f);

        // fit width
        box.setMin(one,  2f / 16f);
        box.setMax(one, 14f / 16f);

        return box;
    }
}
