package net.toshayo.waterframes.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.network.packets.*;

public class PacketDispatcher {
    public static final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(WaterFramesMod.MOD_ID);

    public static void registerPackets() {
        int i = 0;

        wrapper.registerMessage(TimePacket.Handler.class, TimePacket.class, i, Side.SERVER);
        wrapper.registerMessage(TimePacket.Handler.class, TimePacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(VolumePacket.Handler.class, VolumePacket.class, i, Side.SERVER);
        wrapper.registerMessage(VolumePacket.Handler.class, VolumePacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(PausePacket.Handler.class, PausePacket.class, i, Side.SERVER);
        wrapper.registerMessage(PausePacket.Handler.class, PausePacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(MutePacket.Handler.class, MutePacket.class, i, Side.SERVER);
        wrapper.registerMessage(MutePacket.Handler.class, MutePacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(LoopPacket.Handler.class, LoopPacket.class, i, Side.SERVER);
        wrapper.registerMessage(LoopPacket.Handler.class, LoopPacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(ActivePacket.Handler.class, ActivePacket.class, i, Side.SERVER);
        wrapper.registerMessage(ActivePacket.Handler.class, ActivePacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(RequestDisplayInfoPacket.Handler.class, RequestDisplayInfoPacket.class, i, Side.SERVER);
        wrapper.registerMessage(RequestDisplayInfoPacket.Handler.class, RequestDisplayInfoPacket.class, i++, Side.CLIENT);

        wrapper.registerMessage(SyncPacket.Handler.class, SyncPacket.class, i++, Side.SERVER);
    }
}
