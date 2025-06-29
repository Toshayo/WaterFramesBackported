package net.toshayo.waterframes;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.RequestDisplayInfoPacket;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import org.apache.commons.lang3.tuple.Triple;

public class WaterFramesCommand extends CommandBase {
    @Override
    public String getName() {
        return "waterframes";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/waterframes <url|width|height> <value> <x> <y> <z>";
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 5) {
            sender.sendMessage(new TextComponentString("Invalid arguments"));
            return;
        }

        net.minecraft.util.math.Vec3d pos = sender.getPositionVector();
        int x = (int) parseCoordinate(pos.x, args[2], true).getResult();
        int y = (int) parseCoordinate(pos.y, args[3], true).getResult();
        int z = (int) parseCoordinate(pos.z, args[4], true).getResult();
        TileEntity tileEntity = sender.getEntityWorld().getTileEntity(new BlockPos(x, y, z));
        if (!(tileEntity instanceof DisplayTileEntity)) {
            sender.sendMessage(new TextComponentString("Block is not a display!"));
            return;
        }
        DisplayTileEntity tile = ((DisplayTileEntity) tileEntity);

        switch (args[0]) {
            case "status":
                sender.sendMessage(new TextComponentString("URL: " + tile.data.uri.toString()));
                break;
            case "url":
                if (!tile.data.uri.toString().equals(args[1])) {
                    tile.data.tick = 0;
                    tile.data.tickMax = -1;
                }
                tile.data.uri = WaterFramesMod.createURI(args[1]);
                tile.data.uuid = sender instanceof EntityPlayer ? ((EntityPlayer) sender).getUniqueID() : DisplayData.NIL_UUID;
                break;
            case "width":
                if (!tile.caps.resizes()) {
                    sender.sendMessage(new TextComponentString("Cannot be resized"));
                }
                if (args[1].equals("auto")) {
                    if (sender instanceof EntityPlayerMP) {
                        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(x, y, z), "wa");
                        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(tile.getWorld().provider.getDimension(), x, y, z, 0, 0), (EntityPlayerMP) sender);
                    }
                } else {
                    tile.data.setWidth(parseInt(args[1]));
                }
                break;
            case "height":
                if (!tile.caps.resizes()) {
                    sender.sendMessage(new TextComponentString("Cannot be resized"));
                }
                if (args[1].equals("auto")) {
                    if (sender instanceof EntityPlayerMP) {
                        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(x, y, z), "ha");
                        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(tile.getWorld().provider.getDimension(), x, y, z, 0, 0), (EntityPlayerMP) sender);
                    }
                } else {
                    tile.data.setHeight(parseInt(args[1]));
                }
                break;
        }
        tile.markDirty();
        sender.sendMessage(new TextComponentString("Success"));
    }
}
