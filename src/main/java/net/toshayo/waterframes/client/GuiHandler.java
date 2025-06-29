package net.toshayo.waterframes.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.toshayo.waterframes.client.gui.DisplayGui;
import net.toshayo.waterframes.client.gui.RemoteControlGui;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (tileEntity instanceof DisplayTileEntity) {
            if(ID == 0) {
                return new DisplayGui(((DisplayTileEntity) tileEntity));
            } else if(ID == 1) {
                return new RemoteControlGui(((DisplayTileEntity) tileEntity));
            }
        }
        return null;
    }
}
