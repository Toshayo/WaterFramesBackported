package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class MutePacket extends AbstractDisplayNetworkPacket {
    public boolean muted;

    public MutePacket(int dimId, int x, int y, int z, boolean muted) {
        super(dimId, x, y, z);
        this.muted = muted;
    }

    public MutePacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        muted = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(muted);
    }

    public static class Handler implements IMessageHandler<MutePacket, IMessage> {
        @Override
        public IMessage onMessage(MutePacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
