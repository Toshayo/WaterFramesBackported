package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class ActivePacket extends AbstractDisplayNetworkPacket {
    public boolean active;

    public ActivePacket(int dimId, int x, int y, int z, boolean active) {
        super(dimId, x, y, z);
        this.active = active;
    }

    public ActivePacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(active);
    }

    public static class Handler implements IMessageHandler<ActivePacket, IMessage> {
        @Override
        public IMessage onMessage(ActivePacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
