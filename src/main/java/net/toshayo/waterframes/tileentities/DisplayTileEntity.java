package net.toshayo.waterframes.tileentities;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.toshayo.waterframes.DisplayCaps;
import net.toshayo.waterframes.DisplayData;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.blocks.DisplayBlock;
import net.toshayo.waterframes.client.TextureDisplay;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.*;
import org.apache.commons.lang3.tuple.Triple;
import org.watermedia.api.image.ImageAPI;
import org.watermedia.api.image.ImageCache;
import org.watermedia.api.math.MathAPI;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;
import toshayopack.team.creative.creativecore.common.util.math.Axis;
import toshayopack.team.creative.creativecore.common.util.math.Facing;

import java.net.URI;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")})
public abstract class DisplayTileEntity extends TileEntity implements SimpleComponent {
    private static int lagTickTime;
    private static int lagTickCompensate;

    public final DisplayData data;
    public final DisplayCaps caps;

    public EnumFacing blockFacing;

    @SideOnly(Side.CLIENT)
    public ImageCache imageCache;
    @SideOnly(Side.CLIENT)
    public TextureDisplay display;
    @SideOnly(Side.CLIENT)
    private boolean isReleased;

    // this is more a runtime-block calculation variables, doesn't fix on DisplayData
    private int lightLevel = 0;

    private boolean isVisible;


    public DisplayTileEntity(DisplayData data, DisplayCaps caps) {
        this.data = data;
        this.caps = caps;

        blockFacing = EnumFacing.DOWN;
        isVisible = true;
    }

    public static void setLagTickTime(long ltt) {
        if (ltt < 60000) {
            lagTickTime = (int) (ltt / 50);
        } else {
            WaterFramesMod.LOGGER.warn("Rejected tick correction of {}ms, overpass watchdog time", ltt);
        }
    }

    public static void clearLagTickTime() {
        lagTickCompensate += lagTickTime;
        lagTickTime = 0;
    }

    @SubscribeEvent
    public static void onTickLast(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            clearLagTickTime();
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureDisplay activeDisplay() {
        return display;
    }

    @SideOnly(Side.CLIENT)
    public TextureDisplay requestDisplay() {
        if (!this.data.active || (!this.data.hasUri() && display != null)) {
            this.cleanDisplay();
            return null;
        }

        if (this.isReleased) {
            this.imageCache = null;
            return null;
        }

        if(imageCache == null && !this.data.hasUri()) {
            cleanDisplay();
            return null;
        }

        if (this.imageCache == null || (this.data.hasUri() && !this.imageCache.uri.equals(this.data.getUri()))) {
            this.imageCache = ImageAPI.getCache(this.data.getUri(), Minecraft.getMinecraft()::func_152344_a);
            this.cleanDisplay();
        }
        switch (imageCache.getStatus()) {
            case LOADING:
            case FAILED:
            case READY:
                if (this.display != null) {
                    return this.display;
                }
                return this.display = new TextureDisplay(this);

            case WAITING:
                this.cleanDisplay();
                this.imageCache.load();
                return display;

            case FORGOTTEN:
                WaterFramesMod.LOGGER.warn("Cached picture is forgotten, cleaning and reloading");
                this.imageCache = null;
                return null;

            default:
                WaterFramesMod.LOGGER.warn("WATERMeDIA Behavior is modified, this shouldn't be executed");
                return null;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("BLOCK_FACING", blockFacing.ordinal());
        data.save(nbt, this);
        nbt.setBoolean("IS_VISIBLE", isVisible);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        blockFacing = EnumFacing.getFront(nbt.getInteger("BLOCK_FACING"));
        data.load(nbt, this);

        isVisible = nbt.getBoolean("IS_VISIBLE");
    }

    @SideOnly(Side.CLIENT)
    private void cleanDisplay() {
        if (this.display != null) {
            this.display.release();
            this.display = null;
        }
    }

    @SideOnly(Side.CLIENT)
    private void release() {
        this.cleanDisplay();
        this.isReleased = true;
    }

    @SideOnly(Side.CLIENT)
    public AlignedBox getRenderBox() {
        return this.caps.getBox(this, blockFacing, getAttachedFace(), true);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
        // FIXME
        //return this.getRenderBox().getBB(xCoord, yCoord, zCoord);
    }

    @Override
    public void invalidate() { // setRemoved
        if (this.isClient() && display != null && !display.isCameraMode()) {
            this.release();
        }
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        if (this.isClient()) {
            this.release();
        }
        super.onChunkUnload();
    }

    public int getLightLevel() {
        return lightLevel;
    }

    private int getLightLevel$internal() {
        return !this.data.hasUri() ? 0 : (int) (((float) this.data.brightness / 255f) * 15);
    }

    public void setActive(boolean mode) {
        PacketDispatcher.wrapper.sendToServer(new ActivePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, mode));
    }

    public void setMute(boolean mode) {
        PacketDispatcher.wrapper.sendToServer(new MutePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, mode));
    }

    public void setPause(boolean pause) {
        PacketDispatcher.wrapper.sendToServer(new PausePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, pause, this.data.tick));
    }

    public void setStop() {
        PacketDispatcher.wrapper.sendToServer(new PausePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, true, 0));
    }

    public void volumeUp() {
        PacketDispatcher.wrapper.sendToServer(new VolumePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, this.data.volume + 5));
    }

    public void volumeDown() {
        PacketDispatcher.wrapper.sendToServer(new VolumePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, this.data.volume - 5));
    }

    public void fastFoward() {
        PacketDispatcher.wrapper.sendToServer(new TimePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, Math.min(data.tick + MathAPI.msToTick(5000), this.data.tickMax), this.data.tickMax));
    }

    public void rewind() {
        PacketDispatcher.wrapper.sendToServer(new TimePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, Math.max(data.tick - MathAPI.msToTick(5000), 0), this.data.tickMax));
    }

    public void nextUri() {
        PacketDispatcher.wrapper.sendToServer(new NextPacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord));
    }

    public void prevUri() {
        PacketDispatcher.wrapper.sendToServer(new PreviousPacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord));
    }

    public void syncTime(int tick, int maxTick) {
        PacketDispatcher.wrapper.sendToServer(new TimePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, tick, maxTick));
    }

    public void loop(boolean loop) {
        PacketDispatcher.wrapper.sendToServer(new LoopPacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, loop));
    }

    @Override
    public void updateEntity() {
        if (this.data.tickMax == -1 || this.data.tick < 0) {
            this.data.tick = 0;
        }

        if (!this.data.paused && this.data.active) {
            if (this.data.tick < this.data.tickMax) {
                if (lagTickCompensate <= 0) {
                    this.data.tick++;
                } else {
                    lagTickCompensate--;
                }
                if (lagTickTime != 0 && this.isServer()) {
                    int ticks = this.data.tick + lagTickTime;
                    while (ticks > this.data.tickMax) {
                        ticks -= this.data.tickMax;
                    }
                    this.data.tick = ticks;
                    this.markDirty();
                }
            } else {
                if (this.data.loop || this.data.tickMax == -1) {
                    this.data.tick = 0;
                }
                if (!this.data.loop && this.data.tickMax != -1) {
                    this.data.nextUri();
                }
            }
        }

        boolean updateBlock = false; // refresh
        int redstoneOutput = 0;

        if (this.data.tickMax > 0 && this.data.active) {
            redstoneOutput = Math.round(((float) this.data.tick / (float) this.data.tickMax) * (14)) + 1;
        }

        boolean lightOnPlay = WFConfig.forceLightOnPlay() || WFConfig.useLightOnPlay() && data.lit;
        int calculatedLight = getLightLevel$internal();
        if (lightOnPlay) {
            if(this.lightLevel != calculatedLight) {
                lightLevel = calculatedLight;
                updateBlock = true;
            }
        } else if(lightLevel > 0) {
            lightLevel = 0;
            updateBlock = true;
        }

        /*if (state.getValue(DisplayBlock.POWER) != redstoneOutput) {
            state = state.setValue(DisplayBlock.POWER, redstoneOutput);
            updateBlock = true;
        }*/

        if (updateBlock) {
            markDirty();
        }

        if (this.isClient()) {
            TextureDisplay display = this.requestDisplay();
            if (display != null && display.canTick()) {
                display.tick(xCoord, yCoord, zCoord);
            }
        }
    }

    public boolean isClient() {
        return this.worldObj != null && this.worldObj.isRemote;
    }

    public boolean isServer() {
        return this.worldObj != null && !this.worldObj.isRemote;
    }

    public boolean canHideModel() {
        return ((DisplayBlock) getBlockType()).canHideModel();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisibility(boolean visible) {
        isVisible = visible;
        markDirty();
    }

    @Override
    public void markDirty() {
        if (this.worldObj != null) {
            super.markDirty();
            worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord); // Might be already done in block update, not sure
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
        } else {
            WaterFramesMod.LOGGER.warn("Cannot be stored block data, world is NULL");
        }
    }

    public static AlignedBox getBasicBox(DisplayTileEntity tile) {
        final Facing facing = Facing.get(tile instanceof FrameTileEntity ? tile.getAttachedFace() : tile.blockFacing);
        final AlignedBox box = new AlignedBox();

        if (facing.positive) {
            box.setMax(facing.axis, tile.data.projectionDistance);
        } else {
            box.setMin(facing.axis, 1 - tile.data.projectionDistance);
        }

        Axis one = facing.one();
        Axis two = facing.two();
        if (facing.axis != Axis.Z) {
            one = facing.two();
            two = facing.one();
        }

        box.setMin(one, tile.data.min.x);
        box.setMax(one, tile.data.max.x);

        box.setMin(two, tile.data.min.y);
        box.setMax(two, tile.data.max.y);

        if ((tile.caps.projects() && (facing.toVanilla() == EnumFacing.NORTH || facing.toVanilla() == EnumFacing.EAST)) ||
                (!tile.caps.projects() && (facing.toVanilla() == EnumFacing.WEST || facing.toVanilla() == EnumFacing.SOUTH))) {
            switch (tile.data.getPosX()) {
                case LEFT:
                    box.setMin(one, 1 - tile.data.getWidth());
                    box.setMax(one, 1);
                    break;
                case RIGHT:
                    box.setMax(one, tile.data.getWidth());
                    box.setMin(one, 0f);
                    break;
            }
        }
        return box;
    }

    public boolean isPowered() {
        // TODO
        //return this.getBlockState().getValue(DisplayBlock.POWERED);
        return false;
    }

    public EnumFacing getAttachedFace() {
        if(worldObj != null) {
            return EnumFacing.getFront(getBlockMetadata());
        }
        return EnumFacing.SOUTH;
    }

    @Override
    @Optional.Method(modid = "OpenComputers")
    public String getComponentName() {
        return "waterframe";
    }

    @Callback(doc = "function():string -- Get display URL.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] getURL(Context context, Arguments args) {
        return new Object[] { data.getUri().toString() };
    }

    @Callback(doc = "function(url:string):nil -- Set display URL.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] setURL(Context context, Arguments args) {
        URI uri = URI.create(args.checkString(0));
        if (!data.getUri().equals(uri)) {
            data.tick = 0;
            data.tickMax = -1;
        }
        data.setUri(uri);
        data.uuid = DisplayData.NIL_UUID;
        markDirty();
        return new Object[] {};
    }

    @Callback(doc = "function():number -- Get display width.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] getWidth(Context context, Arguments args) {
        return new Object[] { data.getWidth() };
    }

    @Callback(doc = "function(width:number):nil -- Set display width.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] setWidth(Context context, Arguments args) {
        data.setWidth(args.checkInteger(0));
        markDirty();
        return new Object[] {};
    }

    @Callback(doc = "function():number -- Get display height.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] getHeight(Context context, Arguments args) {
        return new Object[] { data.getHeight() };
    }

    @Callback(doc = "function(height:number):nil -- Set display height.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] setHeight(Context context, Arguments args) {
        data.setHeight(args.checkInteger(0));
        markDirty();
        return new Object[] {};
    }

    @Callback(doc = "function():boolean -- Set automatically display width. Returns true on success, false otherwise.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] setAutoWidth(Context context, Arguments args) {
        if(worldObj.playerEntities.isEmpty()) {
            return new Object[] { false };
        }
        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(xCoord, yCoord, zCoord), "wa");
        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 0, 0), ((EntityPlayerMP) worldObj.playerEntities.get(0)));
        return new Object[] { true };
    }

    @Callback(doc = "function():boolean -- Set automatically display height. Returns true on success, false otherwise.")
    @Optional.Method(modid = "OpenComputers")
    public Object[] setAutoHeight(Context context, Arguments args) {
        if(worldObj.playerEntities.isEmpty()) {
            return new Object[] { false };
        }
        RequestDisplayInfoPacket.PENDING_RESIZES.put(Triple.of(xCoord, yCoord, zCoord), "ha");
        PacketDispatcher.wrapper.sendTo(new RequestDisplayInfoPacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 0, 0), ((EntityPlayerMP) worldObj.playerEntities.get(0)));
        return new Object[] { true };
    }
}
