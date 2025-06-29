package net.toshayo.waterframes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public abstract class DisplayBlock extends Block implements ITileEntityProvider {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    protected DisplayBlock() {
        super(Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        setHarvestLevel("pickaxe", 1);
        setHardness(1.5F);
        setResistance(5);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int p_176203_1_) {
        EnumFacing lvt_2_1_ = EnumFacing.getFront(p_176203_1_);
        if (lvt_2_1_.getAxis() == EnumFacing.Axis.Y) {
            lvt_2_1_ = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, lvt_2_1_);
    }

    @Override
    public int getMetaFromState(IBlockState p_176201_1_) {
        return p_176201_1_.getValue(FACING).getIndex();
    }

    @Override
    public boolean isOpaqueCube(IBlockState p_149662_1_) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState p_isNormalCube_1_, IBlockAccess p_isNormalCube_2_, BlockPos p_isNormalCube_3_) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState p_149721_1_) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState p_149637_1_) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            player.openGui(WaterFramesMod.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float p_getStateForPlacement_4_, float p_getStateForPlacement_5_, float p_getStateForPlacement_6_, int p_getStateForPlacement_7_, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase placer, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, blockState, placer, itemStack);
        world.setBlockState(pos, blockState.withProperty(FACING, placer.getHorizontalFacing()), 2);

        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof DisplayTileEntity) {
            ((DisplayTileEntity) tileEntity).blockFacing = placer.getHorizontalFacing();
            tileEntity.markDirty();
        }
    }

    @Override
    public int getLightValue(IBlockState p_getLightValue_1_, IBlockAccess p_getLightValue_2_, BlockPos p_getLightValue_3_) {
        return super.getLightValue(p_getLightValue_1_, p_getLightValue_2_, p_getLightValue_3_);
    }

    public boolean canHideModel() {
        return false;
    }
}
