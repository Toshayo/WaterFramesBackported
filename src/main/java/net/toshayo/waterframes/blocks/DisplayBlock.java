package net.toshayo.waterframes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public abstract class DisplayBlock extends Block implements ITileEntityProvider {
    protected DisplayBlock() {
        super(Material.iron);

        setHarvestLevel("pickaxe", 1);
        setHardness(1.5F);
        setResistance(5);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            player.openGui(WaterFramesMod.INSTANCE, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        if(side == 4) {
            side = 5;
        } else if(side == 5) {
            side = 4;
        }
        return side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, placer, itemStack);

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity instanceof DisplayTileEntity) {
            int direction = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch (direction) {
                case 0:
                    direction = 3;
                    break;
                case 1:
                    direction = 5;
                    break;
                case 3:
                    direction = 4;
                    break;
            }
            int meta = world.getBlockMetadata(x, y, z);
            if(placer.isSneaking() && direction == EnumFacing.getFront(meta).order_b) {
                world.setBlockMetadataWithNotify(x, y, z, EnumFacing.getFront(meta).order_b, 3);
            }
            ((DisplayTileEntity) tileEntity).blockFacing = EnumFacing.getFront(direction);
            tileEntity.markDirty();
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof DisplayTileEntity) {
            return ((DisplayTileEntity) tile).getLightLevel();
        }
        return 0;
    }

    public boolean canHideModel() {
        return false;
    }
}
