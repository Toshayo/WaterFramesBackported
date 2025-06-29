package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class TimePacket extends AbstractDisplayNetworkPacket {
    public int tick;
    public int tickMax;

    public TimePacket(int dimId, int x, int y, int z, int tick, int tickMax) {
        super(dimId, x, y, z);
        this.tick = tick;
        this.tickMax = tickMax;
    }

    public TimePacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        tick = buf.readInt();
        tickMax = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(tick);
        buf.writeInt(tickMax);
    }

    public static class Handler implements IMessageHandler<TimePacket, IMessage> {
        @Override
        public IMessage onMessage(TimePacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
