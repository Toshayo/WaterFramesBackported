package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.toshayo.waterframes.WaterFramesMod;

import java.io.IOException;

public class SyncPacket extends AbstractDisplayNetworkPacket {
    public NBTTagCompound nbt;

    public SyncPacket(int dimId, int x, int y, int z, NBTTagCompound nbt) {
        super(dimId, x, y, z);
        this.nbt = nbt;
    }

    public SyncPacket() {
        super();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        try {
            nbt = new PacketBuffer(buf).readCompoundTag();
        } catch (IOException e) {
            WaterFramesMod.LOGGER.error("Failed to parse NBT of SyncPacket: {}", e.getMessage());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        new PacketBuffer(buf).writeCompoundTag(nbt);
    }

    public static class Handler implements IMessageHandler<SyncPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncPacket message, MessageContext ctx) {
            WaterFramesMod.proxy.handlePacket(message, ctx);
            return null;
        }
    }
}
