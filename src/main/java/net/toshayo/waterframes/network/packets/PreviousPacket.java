package net.toshayo.waterframes.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class PreviousPacket  extends AbstractDisplayNetworkPacket {
    public PreviousPacket(int dimId, int x, int y, int z) {
        super(dimId, x, y, z);
    }

    public PreviousPacket() {
        super();
    }

    public static class Handler implements IMessageHandler<PreviousPacket, IMessage> {
        @Override
        public IMessage onMessage(PreviousPacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
