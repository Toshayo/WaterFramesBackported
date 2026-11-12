package net.toshayo.waterframes.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import net.toshayo.waterframes.tileentities.TVBoxTileEntity;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.Axis;
import toshayopack.team.creative.creativecore.common.util.math.Facing;

public class TVBoxBlock extends DisplayBlock {
    private static final AlignedBox STATIC_BOX = new AlignedBox();

    @SideOnly(Side.CLIENT)
    private IIcon front_icon;

    public TVBoxBlock() {
        super();

        setBlockTextureName("noteblock");
        setBlockName("tv_box");
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        front_icon = reg.registerIcon(WaterFramesMod.MOD_ID + ":tv_box");
        super.registerBlockIcons(reg);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 4 ? front_icon : super.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if(tile instanceof DisplayTileEntity displayTile) {
            if(switch(displayTile.blockFacing) {
                case NORTH, SOUTH -> displayTile.blockFacing.order_b;
                default -> displayTile.blockFacing.ordinal();
            } == side) {
                return front_icon;
            }
        }
        return blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TVBoxTileEntity();
    }

    public static AlignedBox box(EnumFacing direction, boolean renderMode) {
        if (!renderMode) return STATIC_BOX;

        Facing facing = Facing.get(EnumFacing.getFront(direction.order_b));
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
