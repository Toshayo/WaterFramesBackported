package net.toshayo.waterframes.blocks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.FrameTileEntity;

public class FrameBlock extends DisplayBlock {
    public static final float THICKNESS = 0.0625F / 2F;

    public FrameBlock() {
        super();

        setRegistryName(new ResourceLocation(WaterFramesMod.MOD_ID, "frame"));
        setUnlocalizedName("frame");
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        switch (facing) {
            case DOWN:
                return new AxisAlignedBB(0, 0, 0, 1, THICKNESS, 1);
            case UP:
                return new AxisAlignedBB(0, 1 - THICKNESS, 0, 1, 1, 1);
            case NORTH:
                return new AxisAlignedBB(0, 0, 0, 1, 1, THICKNESS);
            case SOUTH:
                return new AxisAlignedBB(0, 0, 1 - THICKNESS, 1, 1, 1);
            case WEST:
                return new AxisAlignedBB(0, 0, 0, THICKNESS, 1, 1);
            case EAST:
                return new AxisAlignedBB(1 - THICKNESS, 0, 0, 1, 1, 1);
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return getBoundingBox(state, worldIn, pos); // Same as visual bounds
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return getBoundingBox(state, worldIn, pos).offset(pos);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new FrameTileEntity();
    }

    @Override
    public boolean canHideModel() {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(
                this,
                super.createBlockState().getProperties().toArray(new IProperty[0]),
                new IUnlistedProperty[]{ ExtendedBlockStateProvider.TILE }
        );
    }

    @Override
    public IBlockState getExtendedState(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (blockState instanceof IExtendedBlockState && te != null) {
            return ((IExtendedBlockState) blockState).withProperty(ExtendedBlockStateProvider.TILE, te);
        }

        return blockState;
    }
}
