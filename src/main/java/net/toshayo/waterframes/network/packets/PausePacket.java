package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class PausePacket extends AbstractDisplayNetworkPacket {
    public boolean paused;
    public int tick;

    public PausePacket(int dimId, int x, int y, int z, boolean paused, int tick) {
        super(dimId, x, y, z);
        this.paused = paused;
        this.tick = tick;
    }

    public PausePacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        paused = buf.readBoolean();
        tick = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(paused);
        buf.writeInt(tick);
    }

    public static class Handler implements IMessageHandler<PausePacket, IMessage> {
        @Override
        public IMessage onMessage(PausePacket message, MessageContext ctx) {
            WaterFramesMod.LOGGER.info("Handling Pause packet");
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
