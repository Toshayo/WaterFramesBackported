package net.toshayo.waterframes.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.TVTileEntity;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.Axis;
import toshayopack.team.creative.creativecore.common.util.math.Facing;

public class TVBlock extends DisplayBlock {
    public TVBlock() {
        super();

        setRegistryName(new ResourceLocation(WaterFramesMod.MOD_ID, "tv"));
        setUnlocalizedName("tv");
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TVTileEntity();
    }

    public static AlignedBox box(EnumFacing direction, EnumFacing attachedBlockFace, boolean renderMode) {
        Facing facing = Facing.get(direction);
        Facing wide = Facing.get(attachedBlockFace);
        AlignedBox box = new AlignedBox();

        // FIXME: re-add tv wall/ceiling attachments
        attachedBlockFace = EnumFacing.UP;

        // SETUP PROFUNDITY
        float renderMargin = renderMode ? 1f : 0;
        if (attachedBlockFace.getOpposite() == direction) {
            if (facing.positive) {
                box.setMax(facing.axis, 1f - (4f / 16.0f));
                box.setMin(facing.axis, 1f - (6f / 16.0f));
            } else {
                box.setMax(facing.axis, (6f / 16.0f));
                box.setMin(facing.axis, (4f / 16.0f));
            }
        } else if (attachedBlockFace == direction) {
            if (facing.positive) {
                box.setMax(facing.axis, (3f / 16.0f));
                box.setMin(facing.axis, (1f / 16.0f));
            } else {
                box.setMax(facing.axis, 1f - (2f / 16.0f));
                box.setMin(facing.axis, 1f - (4f / 16.0f));
                box.setMax(facing.axis, 1f - (1f / 16.0f));
                box.setMin(facing.axis, 1f - (3f / 16.0f));
            }
        } else if (attachedBlockFace == EnumFacing.UP) {
            if (facing.positive) {
                box.setMax(facing.axis, (6f / 16.0f));
                box.setMin(facing.axis, (4f / 16.0f));
            } else {
                box.setMax(facing.axis, 1f - (4.0f / 16.0f));
                box.setMin(facing.axis, 1f - (6.0f / 16.0f));
            }
        } else {
            if (facing.positive) {
                box.setMax(facing.axis, (-1.0f / 16.0f));
                box.setMin(facing.axis, (-3.0f / 16.0f));
            } else {
                box.setMax(facing.axis, 1f - (-3.0f / 16.0f));
                box.setMin(facing.axis, 1f - (-1.0f / 16.0f));
            }
        }

        Axis one = facing.one();
        Axis two = facing.two();

        if (facing.axis != Axis.Z) {
            one = facing.two();
            two = facing.one();
        }

        // SETUP HEIGHT
        if (attachedBlockFace == EnumFacing.DOWN) {
            box.setMax(two, ((12f - renderMargin) / 16f)); // render: 9.5
            box.setMin(two, -((9f - renderMargin) / 16f)); // render: 9.5
        } else if (attachedBlockFace == EnumFacing.UP) {
            box.setMax(two, ((24f - renderMargin) / 16f));
            box.setMin(two, (3f + renderMargin) / 16f);
        } else if (attachedBlockFace.getOpposite() == direction || attachedBlockFace == direction) {
            box.setMax(two, ((19f - renderMargin) / 16f));
            box.setMin(two, (-2f + renderMargin) / 16f);
        } else {
            box.setMax(two, ((19.0f - renderMargin) / 16.0f)); // render: 22.5
            box.setMin(two, ((-2.0f + renderMargin) / 16.0f)); // render: 3.5
        }

        // SETUP WIDE
        if (attachedBlockFace == EnumFacing.DOWN || attachedBlockFace == EnumFacing.UP) {
            box.setMax(one, ((25f - renderMargin) / 16f)); // render: 23
            box.setMin(one, 1 - ((25f - renderMargin) / 16f)); // render: 23
        } else if (attachedBlockFace.getOpposite() == direction || attachedBlockFace == direction) {
            box.setMax(one, ((25f - renderMargin) / 16f));
            box.setMin(one, 1 - ((25f - renderMargin) / 16f));
        } else {
            if (wide.positive) {
                box.setMax(one, (32f - renderMargin) / 16f); // render: 31f / 16f
                box.setMin(one, (-2f + renderMargin) / 16f); // render: 1f / 16f
            } else {
                box.setMax(one, (18f - renderMargin) / 16f); // render: 15f / 16f
                box.setMin(one, -((16f - renderMargin) / 16f)); // render: 15f / 16f
            }
        }

        if (!renderMode) {
            box.scale(1.01f);
        }
        return box;
    }
}
