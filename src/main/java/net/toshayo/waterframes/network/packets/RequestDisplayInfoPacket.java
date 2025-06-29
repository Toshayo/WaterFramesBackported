package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;

public class RequestDisplayInfoPacket extends AbstractDisplayNetworkPacket {
    public int width, height;

    public static final HashMap<Triple<Integer, Integer, Integer>, String> PENDING_RESIZES = new HashMap<>();

    public RequestDisplayInfoPacket(int dimId, int x, int y, int z, int width, int height) {
        super(dimId, x, y, z);
        this.width = width;
        this.height = height;
    }

    public RequestDisplayInfoPacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        width = buf.readInt();
        height = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(width);
        buf.writeInt(height);
    }

    public static class Handler implements IMessageHandler<RequestDisplayInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(RequestDisplayInfoPacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
