package net.toshayo.waterframes.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.client.DisplayControl;
import net.toshayo.waterframes.client.FrameBakedModel;
import net.toshayo.waterframes.client.render.tileentity.*;
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
        try {
            Class.forName("com.sun.jna.Platform");
        } catch (ClassNotFoundException ignored) {
            throw new RuntimeException("JNA library missing!\r\nPlease download the library from the two links (jna and jna-platform) and put the jars into mods folder.\r\n" +
                    "- https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar\r\n" +
                    "- https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.10.0/jna-platform-5.10.0.jar\r\n");
        }
        WaterMedia.prepare(ILoader.DEFAULT).start();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        DisplayControl displayControl = new DisplayControl();
        FMLCommonHandler.instance().bus().register(displayControl);
        MinecraftForge.EVENT_BUS.register(displayControl);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(FrameTileEntity.class, new FrameRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(BigTVTileEntity.class, new DisplayRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TVTileEntity.class, new DisplayRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ProjectorTileEntity.class, new DisplayRenderer());
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        WaterFramesMod.LOGGER.info("Baking frame models");
        for(EnumFacing facing : EnumFacing.values()) {
            ModelResourceLocation modelLocation = new ModelResourceLocation(WaterFramesMod.MOD_ID + ":frame", "facing=" + facing.getName());
            IBakedModel existing = event.getModelRegistry().getObject(modelLocation);
            if (existing != null) {
                event.getModelRegistry().putObject(modelLocation, new FrameBakedModel(existing));
            }
        }
    }

    @Override
    public float deltaFrames() {
        return Minecraft.getMinecraft().isGamePaused() ? 1.0F : Minecraft.getMinecraft().getRenderPartialTicks();
    }

    @Override
    public DisplayTileEntity getDisplayTileEntityForPacket(AbstractDisplayNetworkPacket packet, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            try {
                if (Minecraft.getMinecraft().world.provider.getDimension() != packet.dimId) {
                    return null;
                }
                TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(new BlockPos(packet.posX, packet.posY, packet.posZ));
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
        TileEntity tile = Minecraft.getMinecraft().player.world.getTileEntity(new BlockPos(message.posX, message.posY, message.posZ));
        if (!(tile instanceof DisplayTileEntity)) {
            return;
        }
        DisplayTileEntity displayTile = (DisplayTileEntity) tile;
        PacketDispatcher.wrapper.sendToServer(new RequestDisplayInfoPacket(
                tile.getWorld().provider.getDimension(),
                message.posX, message.posY, message.posZ,
                displayTile.display.width(), displayTile.display.height()
        ));
    }
}
