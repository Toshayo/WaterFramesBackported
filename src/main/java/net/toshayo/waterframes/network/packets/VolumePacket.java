package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

public class VolumePacket extends AbstractDisplayNetworkPacket {
    public int volume;

    public VolumePacket(int dimId, int x, int y, int z, int volume) {
        super(dimId, x, y, z);
        this.volume = volume;
    }

    public VolumePacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        volume = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(volume);
    }

    public static class Handler implements IMessageHandler<VolumePacket, IMessage> {
        @Override
        public IMessage onMessage(VolumePacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
