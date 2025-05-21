package net.toshayo.waterframes.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.client.DisplayControl;
import net.toshayo.waterframes.client.render.item.DisplayItemRenderer;
import net.toshayo.waterframes.client.render.item.RemoteControlItemRenderer;
import net.toshayo.waterframes.client.render.tileentity.BigTVRenderer;
import net.toshayo.waterframes.client.render.tileentity.FrameRenderer;
import net.toshayo.waterframes.client.render.tileentity.ProjectorRenderer;
import net.toshayo.waterframes.client.render.tileentity.TVRenderer;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.AbstractDisplayNetworkPacket;
import net.toshayo.waterframes.network.packets.MutePacket;
import net.toshayo.waterframes.network.packets.PausePacket;
import net.toshayo.waterframes.network.packets.RequestDisplayInfoPacket;
import net.toshayo.waterframes.tileentities.*;
import org.watermedia.WaterMedia;
import org.watermedia.loaders.ILoader;
import org.watermedia.videolan4j.tools.Version;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        super.preInit(event);

        WaterFramesMod.LOGGER.info("Using WATERMeDIA version {}", WaterMedia.VERSION);
        final Version minVersion = new Version("2.1.24");
        if(!new Version(WaterMedia.VERSION).atLeast(minVersion)) {
            throw new RuntimeException("WaterMedia " + WaterMedia.VERSION + " is too old, please update the mod");
        }
        WaterMedia.prepare(ILoader.DEFAULT).start();
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        DisplayControl displayControl = new DisplayControl();
        FMLCommonHandler.instance().bus().register(displayControl);
        MinecraftForge.EVENT_BUS.register(displayControl);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) throws Exception {
        super.postInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(FrameTileEntity.class, new FrameRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(BigTVTileEntity.class, new BigTVRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TVTileEntity.class, new TVRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ProjectorTileEntity.class, new ProjectorRenderer());

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(WaterFramesMod.BIG_TV), new DisplayItemRenderer(BigTVTileEntity.class));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(WaterFramesMod.TV), new DisplayItemRenderer(TVTileEntity.class));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(WaterFramesMod.PROJECTOR), new DisplayItemRenderer(ProjectorTileEntity.class));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(WaterFramesMod.FRAME), new DisplayItemRenderer(FrameTileEntity.class));
        MinecraftForgeClient.registerItemRenderer(WaterFramesMod.REMOTE, new RemoteControlItemRenderer());
    }

    @Override
    public float deltaFrames() {
        return Minecraft.getMinecraft().isGamePaused() ? 1.0F : Minecraft.getMinecraft().timer.renderPartialTicks;
    }

    @Override
    public DisplayTileEntity getDisplayTileEntityForPacket(AbstractDisplayNetworkPacket packet, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            try {
                if (Minecraft.getMinecraft().theWorld.provider.dimensionId != packet.dimId) {
                    return null;
                }
                TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(packet.posX, packet.posY, packet.posZ);
                if (tile instanceof DisplayTileEntity) {
                    return (DisplayTileEntity) tile;
                }
            } catch (Exception ignored) {
            }
            return null;
        }
        return super.getDisplayTileEntityForPacket(packet, ctx);
    }

    @Override
    public void handlePacket(MutePacket message, MessageContext ctx) {
        super.handlePacket(message, ctx);
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        if (tile.display != null) {
            tile.display.setMuteMode(message.muted);
        }
    }

    @Override
    public void handlePacket(PausePacket message, MessageContext ctx) {
        super.handlePacket(message, ctx);
        DisplayTileEntity tile = WaterFramesMod.proxy.getDisplayTileEntityForPacket(message, ctx);
        if (tile == null) {
            return;
        }
        if (tile.display != null) { // COPYPASTED from WATERFrAMES TODO: this is redundant, but i have no time to debug this
            tile.display.setPauseMode(message.paused);
        }
    }

    @Override
    public void handlePacket(RequestDisplayInfoPacket message, MessageContext ctx) {
        TileEntity tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.posX, message.posY, message.posZ);
        if (!(tile instanceof DisplayTileEntity)) {
            return;
        }
        DisplayTileEntity displayTile = (DisplayTileEntity) tile;
        PacketDispatcher.wrapper.sendToServer(new RequestDisplayInfoPacket(
                tile.getWorldObj().provider.dimensionId,
                message.posX, message.posY, message.posZ,
                displayTile.display.width(), displayTile.display.height()
        ));
    }
}
