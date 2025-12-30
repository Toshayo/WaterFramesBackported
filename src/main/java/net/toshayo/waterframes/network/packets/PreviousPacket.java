package net.toshayo.waterframes.network.packets;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

public class PreviousPacket extends AbstractDisplayNetworkPacket {
    public PreviousPacket(int dimId, int x, int y, int z) {
        super(dimId, x, y, z);
    }

    public PreviousPacket() {
        super();
    }

    public static class Handler implements IMessageHandler<PreviousPacket, IMessage> {
        @Override
        public IMessage onMessage(PreviousPacket message, MessageContext ctx) {
            DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
            if(tile != null) {
                tile.data.prevUri();
                tile.markDirty();
                if(ctx.side == Side.SERVER) {
                    PacketDispatcher.wrapper.sendToAllAround(new PreviousPacket(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord), new NetworkRegistry.TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, WFConfig.maxRenDis()));
                }
            }
            return null;
        }
    }
}
