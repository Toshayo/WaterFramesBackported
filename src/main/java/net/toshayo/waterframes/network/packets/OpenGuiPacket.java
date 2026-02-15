package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class OpenGuiPacket extends AbstractDisplayNetworkPacket {
    public int id;

    public OpenGuiPacket(int dimId, int x, int y, int z, int id) {
        super(dimId, x, y, z);
        this.id = id;
    }

    public OpenGuiPacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<OpenGuiPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenGuiPacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
