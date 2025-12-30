package net.toshayo.waterframes.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
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

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        WFConfig.init(event.getSuggestedConfigurationFile());

        WaterFramesMod.FRAME = new FrameBlock();
        WaterFramesMod.TV = new TVBlock();
        WaterFramesMod.BIG_TV = new BigTVBlock();
        WaterFramesMod.PROJECTOR = new ProjectorBlock();

        WaterFramesMod.REMOTE = new RemoteControlItem();

        GameRegistry.registerBlock(WaterFramesMod.FRAME, WaterFramesMod.FRAME.getUnlocalizedName());
        GameRegistry.registerBlock(WaterFramesMod.TV, WaterFramesMod.TV.getUnlocalizedName());
        GameRegistry.registerBlock(WaterFramesMod.BIG_TV, WaterFramesMod.BIG_TV.getUnlocalizedName());
        GameRegistry.registerBlock(WaterFramesMod.PROJECTOR, WaterFramesMod.PROJECTOR.getUnlocalizedName());

        GameRegistry.registerItem(WaterFramesMod.REMOTE, WaterFramesMod.REMOTE.getUnlocalizedName());

        GameRegistry.registerTileEntity(FrameTileEntity.class, WaterFramesMod.MOD_ID + ":frame");
        GameRegistry.registerTileEntity(TVTileEntity.class, WaterFramesMod.MOD_ID + ":tv");
        GameRegistry.registerTileEntity(BigTVTileEntity.class, WaterFramesMod.MOD_ID + ":big_tv");
        GameRegistry.registerTileEntity(ProjectorTileEntity.class, WaterFramesMod.MOD_ID + ":projector");

        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(WaterFramesMod.INSTANCE, new GuiHandler());

        FMLCommonHandler.instance().bus().register(this);
    }

    public void onInit(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) throws Exception {
        GameRegistry.addShapedRecipe(new ItemStack(WaterFramesMod.BIG_TV),
                "iIi",
                "iGi",
                "iRi",
                'i', Items.iron_ingot,
                'I', Blocks.iron_block,
                'G', Blocks.stained_glass,
                'R', WaterFramesMod.REMOTE);
        GameRegistry.addShapedRecipe(new ItemStack(WaterFramesMod.FRAME),
                "iGi",
                "iTi",
                "iSi",
                'i', Items.iron_ingot,
                'G', Items.glowstone_dust,
                'T', Blocks.stained_glass,
                'S', WaterFramesMod.REMOTE);
        GameRegistry.addShapedRecipe(new ItemStack(WaterFramesMod.PROJECTOR),
                "##A",
                "#GA",
                "#RA",
                '#', Blocks.iron_block,
                'A', Blocks.redstone_lamp,
                'G', Items.glowstone_dust,
                'R', WaterFramesMod.REMOTE);
        GameRegistry.addShapedRecipe(new ItemStack(WaterFramesMod.REMOTE),
                "ici",
                "iRi",
                "SSS",
                'i', Items.iron_ingot,
                'c', Items.gold_ingot,
                'R', Items.redstone,
                'S', Blocks.stone_button);
        GameRegistry.addShapedRecipe(new ItemStack(WaterFramesMod.TV),
                "iii",
                "iGi",
                "iRi",
                'i', Items.iron_ingot,
                'G', Blocks.stained_glass,
                'R', WaterFramesMod.REMOTE);
    }

    public float deltaFrames() {
        return 0;
    }

    public DisplayTileEntity getDisplayTileEntityForPacket(AbstractDisplayNetworkPacket packet, MessageContext ctx) {
        try {
            TileEntity tile = MinecraftServer.getServer().worldServerForDimension(packet.dimId).getTileEntity(packet.posX, packet.posY, packet.posZ);
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
        if (!tile.data.hasUri()) {
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
        DisplayData.sync(displayTile, ctx.getServerHandler().playerEntity, message.nbt);
    }

    @SubscribeEvent
    public void onTickLast(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            DisplayTileEntity.clearLagTickTime();
        }
    }
}
