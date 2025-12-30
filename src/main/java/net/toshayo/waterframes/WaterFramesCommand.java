package net.toshayo.waterframes;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.RequestDisplayInfoPacket;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import org.apache.commons.lang3.tuple.Triple;

public class WaterFramesCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "waterframes";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/waterframes <url|width|height> <value> <x> <y> <z>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 5) {
            sender.addChatMessage(new ChatComponentText("Invalid arguments"));
            return;
        }

        int x = (int) func_110666_a(sender, sender instanceof EntityPlayer ? ((EntityPlayer) sender).posX : 0, args[2]);
        int y = (int) func_110666_a(sender, sender instanceof EntityPlayer ? ((EntityPlayer) sender).posX : 0, args[3]);
        int z = (int) func_110666_a(sender, sender instanceof EntityPlayer ? ((EntityPlayer) sender).posX : 0, args[4]);
        TileEntity tileEntity = sender.getEntityWorld().getTileEntity(x, y, z);
        if (!(tileEntity instanceof DisplayTileEntity)) {
            sender.addChatMessage(new ChatComponentText("Block is not a display!"));
            return;
        }
        DisplayTileEntity tile = ((DisplayTileEntity) tileEntity);

        switch (args[0]) {
            case "status":
                sender.addChatMessage(new ChatComponentText("URL: " + tile.data.getUri().toString()));
                break;
            case "url":
                if (!tile.data.getUri().toString().equals(args[1])) {
                    tile.data.tick = 0;
                    tile.data.tickMax = -1;
                }
                tile.data.setUri(WaterFramesMod.createURI(args[1]));
                tile.data.uuid = sender instanceof EntityPlayer ? ((EntityPlayer) sender).getUniqueID() : DisplayData.NIL_UUID;
                break;
            case "width":
                if (!tile.caps.resizes()) {
                    sender.addChatMessage(new ChatComponentText("Cannot be resized"));
                }
                if (args[1].equals("auto")) {
                    if (sender instanceof EntityPlayerMP) {
                        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(x, y, z), "wa");
                        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(tile.getWorldObj().provider.dimensionId, x, y, z, 0, 0), (EntityPlayerMP) sender);
                    }
                } else {
                    tile.data.setWidth(parseInt(sender, args[1]));
                }
                break;
            case "height":
                if (!tile.caps.resizes()) {
                    sender.addChatMessage(new ChatComponentText("Cannot be resized"));
                }
                if (args[1].equals("auto")) {
                    if (sender instanceof EntityPlayerMP) {
                        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(x, y, z), "ha");
                        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(tile.getWorldObj().provider.dimensionId, x, y, z, 0, 0), (EntityPlayerMP) sender);
                    }
                } else {
                    tile.data.setHeight(parseInt(sender, args[1]));
                }
                break;
        }
        tile.markDirty();
        sender.addChatMessage(new ChatComponentText("Success"));
    }
}
