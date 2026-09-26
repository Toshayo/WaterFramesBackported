package net.toshayo.waterframes.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class NextPacket  extends AbstractDisplayNetworkPacket {
    public NextPacket(int dimId, int x, int y, int z) {
        super(dimId, x, y, z);
    }

    public NextPacket() {
        super();
    }

    public static class Handler implements IMessageHandler<NextPacket, IMessage> {
        @Override
        public IMessage onMessage(NextPacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
