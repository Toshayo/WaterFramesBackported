package net.toshayo.waterframes.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.toshayo.waterframes.DisplayData;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.blocks.BigTVBlock;
import net.toshayo.waterframes.blocks.FrameBlock;
import net.toshayo.waterframes.blocks.ProjectorBlock;
import net.toshayo.waterframes.blocks.TVBlock;
import net.toshayo.waterframes.client.GuiHandler;
import net.toshayo.waterframes.items.RemoteControlItem;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.*;
import net.toshayo.waterframes.tileentities.*;
import org.apache.commons.lang3.tuple.Triple;

@Mod.EventBusSubscriber(modid = WaterFramesMod.MOD_ID)
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        WFConfig.init(event.getSuggestedConfigurationFile());

        WaterFramesMod.FRAME = new FrameBlock();
        WaterFramesMod.TV = new TVBlock();
        WaterFramesMod.BIG_TV = new BigTVBlock();
        WaterFramesMod.PROJECTOR = new ProjectorBlock();

        WaterFramesMod.REMOTE = new RemoteControlItem();

        GameRegistry.registerTileEntity(FrameTileEntity.class, new ResourceLocation(WaterFramesMod.MOD_ID,"frame"));
        GameRegistry.registerTileEntity(TVTileEntity.class, new ResourceLocation(WaterFramesMod.MOD_ID, "tv"));
        GameRegistry.registerTileEntity(BigTVTileEntity.class, new ResourceLocation(WaterFramesMod.MOD_ID, "big_tv"));
        GameRegistry.registerTileEntity(ProjectorTileEntity.class, new ResourceLocation(WaterFramesMod.MOD_ID, "projector"));

        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(WaterFramesMod.INSTANCE, new GuiHandler());
    }

    @SubscribeEvent
    public static void onRegistryBlock(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(WaterFramesMod.FRAME);
        e.getRegistry().register(WaterFramesMod.TV);
        e.getRegistry().register(WaterFramesMod.BIG_TV);
        e.getRegistry().register(WaterFramesMod.PROJECTOR);
    }

    @SubscribeEvent
    public static void onRegistryItem(RegistryEvent.Register<Item> e) {
        WaterFramesMod.ITEMS.add(WaterFramesMod.REMOTE);
        WaterFramesMod.ITEMS.add(new ItemBlock(WaterFramesMod.FRAME).setRegistryName(WaterFramesMod.FRAME.getRegistryName()));
        WaterFramesMod.ITEMS.add(new ItemBlock(WaterFramesMod.TV).setRegistryName(WaterFramesMod.TV.getRegistryName()));
        WaterFramesMod.ITEMS.add(new ItemBlock(WaterFramesMod.BIG_TV).setRegistryName(WaterFramesMod.BIG_TV.getRegistryName()));
        WaterFramesMod.ITEMS.add(new ItemBlock(WaterFramesMod.PROJECTOR).setRegistryName(WaterFramesMod.PROJECTOR.getRegistryName()));

        WaterFramesMod.ITEMS.forEach(e.getRegistry()::register);
    }

    public void onInit(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public float deltaFrames() {
        return 0;
    }

    public DisplayTileEntity getDisplayTileEntityForPacket(AbstractDisplayNetworkPacket packet, MessageContext ctx) {
        try {
            TileEntity tile = ctx.getServerHandler().player.mcServer.getWorld(packet.dimId).getTileEntity(new BlockPos(packet.posX, packet.posY, packet.posZ));
            if (tile instanceof DisplayTileEntity) {
                return (DisplayTileEntity) tile;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public void handlePacket(ActivePacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        tile.data.active = message.active;
        tile.markDirty();
    }

    public void handlePacket(LoopPacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        tile.data.loop = message.loop;
        tile.markDirty();
    }

    public void handlePacket(MutePacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        tile.data.muted = message.muted;
        tile.markDirty();
    }

    public void handlePacket(PausePacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        tile.data.paused = (WFConfig.useMasterModeRedstone() && tile.isPowered()) || message.paused;
        if (message.tick != -1) {
            tile.data.tick = message.tick;
        }
        tile.markDirty();
    }

    public void handlePacket(TimePacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        if (tile.data.isUriInvalid()) {
            tile.data.tickMax = -1;
            tile.data.tick = 0;
        } else {
            tile.data.tick = message.tick;
            final boolean maxNegative = tile.data.tickMax == -1;
            if (maxNegative) {
                tile.data.tick = 0;
            }

            if (tile.data.tickMax < message.tickMax) {
                tile.data.tickMax = message.tickMax;
                if (!maxNegative) {
                    WaterFramesMod.LOGGER.warn("Received maxTick value major than current one, media differs?.");
                }
            }
        }
        tile.markDirty();
    }

    public void handlePacket(VolumePacket message, MessageContext ctx) {
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        tile.data.volume = WFConfig.maxVol(message.volume);
        tile.markDirty();
    }

    public void handlePacket(RequestDisplayInfoPacket message, MessageContext ctx) {
        DisplayTileEntity displayTile = getDisplayTileEntityForPacket(message, ctx);
        if(displayTile == null) {
            return;
        }
        switch (RequestDisplayInfoPacket.PENDING_RESIZES.get(Triple.of(message.posX, message.posY, message.posZ))) {
            case "wa":
                displayTile.data.setWidth(message.width / (float) message.height / displayTile.data.getHeight());
                displayTile.markDirty();
                RequestDisplayInfoPacket.PENDING_RESIZES.remove(Triple.of(message.posX, message.posY, message.posZ));
                break;
            case "ha":
                displayTile.data.setHeight(message.height / (float) message.width / displayTile.data.getWidth());
                displayTile.markDirty();
                RequestDisplayInfoPacket.PENDING_RESIZES.remove(Triple.of(message.posX, message.posY, message.posZ));
            default:
                break;
        }
    }

    public void handlePacket(SyncPacket message, MessageContext ctx) {
        DisplayTileEntity displayTile = getDisplayTileEntityForPacket(message, ctx);
        if(displayTile == null) {
            return;
        }
        DisplayData.sync(displayTile, ctx.getServerHandler().player, message.nbt);
    }
}
