package net.toshayo.waterframes.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractDisplayNetworkPacket implements IMessage {
    // display pos
    public int dimId, posX, posY, posZ;


    public AbstractDisplayNetworkPacket(int dimId, int x, int y, int z) {
        this.dimId = dimId;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public AbstractDisplayNetworkPacket() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimId = buf.readInt();
        posX = buf.readInt();
        posY = buf.readInt();
        posZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimId);
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
    }
}
