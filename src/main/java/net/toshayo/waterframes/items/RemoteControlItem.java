package net.toshayo.waterframes.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import net.toshayo.waterframes.utils.ChatUtils;

public class RemoteControlItem extends Item {
    public RemoteControlItem() {
        setRegistryName(new ResourceLocation(WaterFramesMod.MOD_ID, "remote"));
        setUnlocalizedName("remote");
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float p_180614_6_, float p_180614_7_, float p_180614_8_) {
        return handleClick(player.getHeldItem(hand), player, world, pos.getX(), pos.getY(), pos.getZ(), true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        handleClick(player.getHeldItem(hand), player, world, 0, 0, 0, false);
        return super.onItemRightClick(world, player, hand);
    }

    private EnumActionResult handleClick(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, boolean isPosValid) {
        if(!WFConfig.canInteractItem(player)) {
            if(world.isRemote) {
                ChatUtils.fatalMessage(player, "waterframes.common.access.denied");
            }
            return EnumActionResult.FAIL;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt != null && nbt.hasNoTags()) {
            if(world.isRemote) {
                ChatUtils.errorMessage(player, "waterframes.remote.bound.failed");
            }
            return EnumActionResult.FAIL;
        }


        if(player.isSneaking()) {
            if(nbt != null && !nbt.hasNoTags()) {
                stack.setTagCompound(null);
                if(world.isRemote) {
                    ChatUtils.successMessage(player, "waterframes.remote.unbound.success");
                }
                return EnumActionResult.SUCCESS;
            } else {
                if(!isPosValid) {
                    return EnumActionResult.FAIL;
                }
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if(!(tile instanceof DisplayTileEntity)) {
                    if(world.isRemote) {
                        ChatUtils.errorMessage(player, "waterframes.remote.display.invalid");
                    }
                    return EnumActionResult.FAIL;
                }
                if(nbt == null) {
                    nbt = new NBTTagCompound();
                }
                nbt.setInteger("DIM", world.provider.getDimension());
                nbt.setIntArray("POS", new int[]{x, y, z});
                stack.setTagCompound(nbt);
                if(world.isRemote) {
                    ChatUtils.successMessage(player, "waterframes.remote.bound.success");
                }
                return EnumActionResult.SUCCESS;
            }
        }

        if(nbt == null || !nbt.hasKey("DIM") || !nbt.hasKey("POS")) {
            if(world.isRemote) {
                ChatUtils.errorMessage(player, "waterframes.remote.bound.failed");
            }
            return EnumActionResult.FAIL;
        }

        int dimId = nbt.getInteger("DIM");
        int[] pos = nbt.getIntArray("POS");
        if(pos.length != 3) {
            if(world.isRemote) {
                ChatUtils.errorMessage(player, "waterframes.remote.bound.failed");
            }
            return EnumActionResult.FAIL;
        }

        if(world.provider.getDimension() != dimId || new Vec3d(pos[0] + 0.5, pos[1] + 0.5, pos[2] + 0.5)
                .squareDistanceTo(player.posX, player.posY, player.posZ) >= WFConfig.maxRcDis() * WFConfig.maxRcDis()) {
            if(world.isRemote) {
                ChatUtils.errorMessage(player, "waterframes.remote.distance.failed");
            }
            return EnumActionResult.FAIL;
        }

        TileEntity tile = world.getTileEntity(new BlockPos(pos[0], pos[1], pos[2]));

        if(!(tile instanceof DisplayTileEntity)) {
            if(world.isRemote) {
                ChatUtils.errorMessage(player, "waterframes.remote.display.invalid");
            }
            return EnumActionResult.FAIL;
        }

        if(world.isRemote)
            player.openGui(WaterFramesMod.INSTANCE, 1, world, pos[0], pos[1], pos[2]);

        return EnumActionResult.FAIL;
    }
}
