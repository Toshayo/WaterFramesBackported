package net.toshayo.waterframes.client;

import com.xcompwiz.lookingglass.api.view.IWorldView;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.compat.lookingglass.LookingGlassAPIProvider;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.WaterFramesMod;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import org.watermedia.api.image.ImageAPI;
import org.watermedia.api.image.ImageCache;
import org.watermedia.api.math.MathAPI;
import org.watermedia.api.player.videolan.VideoPlayer;
import org.watermedia.videolan4j.player.base.State;

import java.util.concurrent.atomic.AtomicLong;

public class TextureDisplay {
    public static final ImageCache VLC_NOT_FOUND = ImageAPI.cacheFailedVLC();

    // MEDIA AND DATA
    private VideoPlayer mediaPlayer;
    private ImageCache imageCache;
    private final DisplayTileEntity tile;
    private boolean notVideo;

    // CONFIG
    private int xCoord, yCoord, zCoord;
    private int currentVolume = 0;
    private final AtomicLong currentLastTime = new AtomicLong(Long.MIN_VALUE);
    private Mode displayMode = Mode.PICTURE;
    private boolean stream = false;
    private boolean synced = false;
    private boolean released = false;

    // SEEK CONTROL (WHEN SLAVISM MODE IS ENABLED)
    private long seekTime;
    private long lastSeekingTime;

    public TextureDisplay(DisplayTileEntity tile) {
        this.tile = tile;
        this.imageCache = tile.imageCache;
        this.xCoord = tile.xCoord;
        this.yCoord = tile.yCoord;
        this.zCoord = tile.zCoord;
        if(tile.data.uri.toString().startsWith("camera://")) {
            displayMode = Mode.CAMERA;
        } else {
            if (this.imageCache.isVideo()) {
                this.switchVideoMode();
            }
        }
    }

    private void switchVideoMode() {
        if(tile.data.uri.toString().startsWith("camera://")) {
            displayMode = Mode.CAMERA;
            return;
        }

        // DO NOT USE VIDEOLAN IF I DONT WANT
        if (!WFConfig.useMultimedia()) {
            this.imageCache = VLC_NOT_FOUND;
            this.displayMode = Mode.PICTURE;
            return;
        }

        if(notVideo) {
            displayMode = Mode.PICTURE;
            return;
        }

        // START
        this.displayMode = Mode.VIDEO;
        this.mediaPlayer = new VideoPlayer(Minecraft.getMinecraft()::func_152344_a);

        // CHECK IF VLC CAN BE USED
        if (mediaPlayer.isBroken()) {
            this.imageCache = VLC_NOT_FOUND;
            this.displayMode = Mode.PICTURE;
            notVideo = true;
            return;
        }

        int x = (int) (xCoord + Facing.offsetsXForSide[this.tile.getBlockMetadata()] * tile.data.audioOffset);
        int y = (int) (yCoord + Facing.offsetsYForSide[this.tile.getBlockMetadata()] * tile.data.audioOffset);
        int z = (int) (zCoord + Facing.offsetsZForSide[this.tile.getBlockMetadata()] * tile.data.audioOffset);

        this.currentVolume = limitVolume(x, y, z, this.tile.data.volume, this.tile.data.minVolumeDistance, this.tile.data.maxVolumeDistance);

        // PLAYER CONFIG
        this.mediaPlayer.setVolume(this.currentVolume);
        this.mediaPlayer.setRepeatMode(this.tile.data.loop);
        this.mediaPlayer.setPauseMode(this.tile.data.paused);
        this.mediaPlayer.setMuteMode(this.tile.data.muted);
        this.mediaPlayer.start(this.tile.data.uri);
        DisplayControl.add(this);
    }

    public int width() {
        return switch (displayMode) {
            case PICTURE -> this.imageCache.getRenderer() != null ? this.imageCache.getRenderer().width : 1;
            case VIDEO -> this.mediaPlayer.width();
            case AUDIO -> 0;
            default -> throw new IllegalArgumentException();
        };
    }

    public int height() {
        return switch (displayMode) {
            case PICTURE -> this.imageCache.getRenderer() != null ? this.imageCache.getRenderer().height : 1;
            case VIDEO -> this.mediaPlayer.height();
            case AUDIO -> 0;
            default -> throw new IllegalArgumentException();
        };
    }

    public int texture() {
        switch (displayMode) {
            case PICTURE:
                return this.imageCache.getRenderer().texture(tile.data.tick, (!tile.data.paused ? MathAPI.tickToMs(WaterFramesMod.proxy.deltaFrames()) : 0), tile.data.loop);
            case VIDEO:
                return mediaPlayer.isBroken() ?
                        imageCache.getRenderer().texture(
                                tile.data.tick,
                                (!tile.data.paused ? MathAPI.tickToMs(WaterFramesMod.proxy.deltaFrames()) : 0),
                                tile.data.loop
                        ) : this.mediaPlayer.texture();
            case AUDIO:
                return 0;
            case CAMERA:
                String cameraPos = tile.data.uri.toString().substring("camera://".length());
                if(cameraPos.startsWith("/")) {
                    cameraPos = cameraPos.substring(1);
                }
                String[] cameraPosParts = cameraPos.split(",");
                try {
                    TileEntity te = tile.getWorldObj().getTileEntity(
                            Integer.parseInt(cameraPosParts[0]),
                            Integer.parseInt(cameraPosParts[1]),
                            Integer.parseInt(cameraPosParts[2])
                    );
                    if(te != null && te.hasWorldObj() && te instanceof TileEntitySecurityCamera) {
                        // Use same format as SecurityCraft
                        cameraPos = te.xCoord + " " + te.yCoord + " " + te.zCoord + " " + tile.getWorldObj().provider.dimensionId;
                        if (SecurityCraft.instance.hasViewForCoords(cameraPos)) {
                            IWorldView view = SecurityCraft.instance.getViewFromCoords(cameraPos).getView();
                            if (view != null) {
                                if (view.isReady() && view.getTexture() != 0) {
                                    view.markDirty();
                                    return view.getTexture();
                                }
                                view.markDirty();
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {}
                return -1;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void preRender() {
        switch (displayMode) {
            case PICTURE:
                break;
            case VIDEO:
                this.mediaPlayer.preRender();
                break;
        }
    }

    public int getTextureId() {
        return texture();
    }

    public int durationInTicks() {
        return MathAPI.msToTick(duration());
    }

    public long duration() {
        return switch (displayMode) {
            case PICTURE -> this.imageCache.getRenderer() != null ? this.imageCache.getRenderer().duration : 0;
            case VIDEO -> this.mediaPlayer.getDuration();
            case AUDIO, CAMERA -> 0;
            default -> throw new IllegalArgumentException();
        };
    }

    public boolean canTick() {
        return switch (displayMode) {
            case PICTURE -> this.imageCache.getStatus().equals(ImageCache.Status.READY);
            case VIDEO -> this.mediaPlayer.isSafeUse() && this.mediaPlayer.isValid();
            case AUDIO -> // MISSING IMPL
                    this.mediaPlayer.isSafeUse();
            case CAMERA -> true;
            default -> throw new IllegalArgumentException();
        };
    }

    public boolean canRender() {
        return switch (displayMode) {
            case PICTURE ->
                    (imageCache.getStatus() == ImageCache.Status.READY && !this.imageCache.isVideo() && tile.data.active) || notVideo;
            case VIDEO -> this.mediaPlayer.isValid() && tile.data.active;
            case AUDIO -> false;
            case CAMERA -> tile.data.active;
            default -> throw new IllegalArgumentException();
        };
    }

    public void syncDuration() {
        if (tile.data.tickMax == -1) {
            tile.data.tick = 0;
        }
        tile.syncTime(tile.data.tick, durationInTicks());
    }

    public void tick(int x, int y, int z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        switch (displayMode) {
            case PICTURE:
                if (imageCache.isVideo() && !notVideo) {
                    switchVideoMode();
                }
                break;
            case VIDEO:
            case AUDIO:
                if(mediaPlayer.isBroken()) {
                    break;
                }
                x += (int) (Facing.offsetsXForSide[tile.getBlockMetadata()] * this.tile.data.audioOffset);
                y += (int) (Facing.offsetsYForSide[tile.getBlockMetadata()] * this.tile.data.audioOffset);
                z += (int) (Facing.offsetsZForSide[tile.getBlockMetadata()] * this.tile.data.audioOffset);
                int volume = limitVolume(x, y, z, this.tile.data.volume, this.tile.data.minVolumeDistance, this.tile.data.maxVolumeDistance);

                if(!seeking() && seekTime != 0) {
                    seekTo(seekTime);
                }

                if (currentVolume != volume) {
                    mediaPlayer.setVolume(currentVolume = volume);
                }
                if (mediaPlayer.isSafeUse() && mediaPlayer.isValid()) {
                    if (mediaPlayer.getRepeatMode() != tile.data.loop) {
                        mediaPlayer.setRepeatMode(tile.data.loop);
                    }
                    if (mediaPlayer.isMuted() != tile.data.muted) {
                        mediaPlayer.setMuteMode(tile.data.muted);
                    }
                    if (!stream && mediaPlayer.isLive()) {
                        stream = true;
                    }

                    boolean mayPause = tile.data.paused || !tile.data.active || Minecraft.getMinecraft().isGamePaused();

                    if (mediaPlayer.isPaused() != mayPause) {
                        mediaPlayer.setPauseMode(mayPause);
                    }
                    if (!stream && mediaPlayer.isSeekAble()) {
                        long time = MathAPI.tickToMs(tile.data.tick) + (!mayPause ? MathAPI.tickToMs(WaterFramesMod.proxy.deltaFrames()) : 0);
                        if (time > mediaPlayer.getTime() && tile.data.loop) {
                            long mediaDuration = mediaPlayer.getMediaInfoDuration();
                            time = (time == 0 || mediaDuration == 0) ? 0 : Math.floorMod(time, mediaPlayer.getMediaInfoDuration());
                        }

                        if (Math.abs(time - mediaPlayer.getTime()) > DisplayControl.SYNC_TIME && Math.abs(time - currentLastTime.get()) > DisplayControl.SYNC_TIME) {
                            currentLastTime.set(time);
                            seekTo(time);
                        }
                    }
                }
                break;
            case CAMERA:
                String cameraPos = tile.data.uri.toString().substring("camera://".length());
                if(cameraPos.startsWith("/")) {
                    cameraPos = cameraPos.substring(1);
                }
                String[] cameraPosParts = cameraPos.split(",");
                try {
                    TileEntity te = tile.getWorldObj().getTileEntity(
                            Integer.parseInt(cameraPosParts[0]),
                            Integer.parseInt(cameraPosParts[1]),
                            Integer.parseInt(cameraPosParts[2])
                    );
                    if(te != null && te.hasWorldObj() && te instanceof TileEntitySecurityCamera) {
                        // Use same format as SecurityCraft
                        cameraPos = te.xCoord + " " + te.yCoord + " " + te.zCoord + " " + tile.getWorldObj().provider.dimensionId;
                        if (!SecurityCraft.instance.hasViewForCoords(cameraPos)) {
                            LookingGlassAPIProvider.createLookingGlassView(
                                    te.getWorldObj(), te.getWorldObj().provider.dimensionId,
                                    te.xCoord, te.yCoord, te.zCoord,
                                    192, 192);
                        }
                    }
                } catch (NumberFormatException ignored) {}
                break;
        }

        if (!synced && canRender()) {
            syncDuration();
            synced = true;
        }
    }

    private void seekTo(long time) {
        if (WFConfig.useSlavismMode() && this.seeking()) {
            this.seekTime = time;
        } else {
            this.mediaPlayer.seekTo(time);
            this.lastSeekingTime = System.currentTimeMillis();
            this.seekTime = 0;
        }
    }

    public boolean seeking() {
        return this.lastSeekingTime + 10000 > System.currentTimeMillis();
    }

    public boolean isReady() {
        if (this.imageCache.getStatus() != ImageCache.Status.READY) {
            return false;
        }
        return switch (displayMode) {
            case PICTURE, CAMERA -> true;
            case VIDEO, AUDIO -> this.imageCache.getStatus() == ImageCache.Status.READY && this.mediaPlayer.isReady();
        };
    }

    public boolean isBuffering() {
        return switch (displayMode) {
            case PICTURE, CAMERA -> false;
            case VIDEO, AUDIO ->
                    mediaPlayer.isBuffering() || mediaPlayer.isLoading() || mediaPlayer.raw().mediaPlayer().status().state() == State.NOTHING_SPECIAL;
        };
    }

    public boolean isNotVideo() {
        if (this.imageCache.getStatus() == ImageCache.Status.FAILED)
            return true;

        return switch (displayMode) {
            case PICTURE, CAMERA -> false;
            case VIDEO, AUDIO -> this.mediaPlayer.isBroken();
        };
    }

    public boolean isCameraMode() {
        return displayMode == Mode.CAMERA;
    }

    public boolean isLoading() {
        if(imageCache.getStatus() == ImageCache.Status.LOADING) {
            return true;
        }

        return switch (displayMode) {
            case PICTURE, CAMERA -> false;
            case VIDEO, AUDIO -> this.mediaPlayer.isLoading();
        };
    }

    public boolean isReleased() {
        return released;
    }

    public void setPauseMode(boolean pause) {
        switch (displayMode) {
            case PICTURE:
                break;
            case VIDEO:
            case AUDIO:
                seekTo(MathAPI.tickToMs(this.tile.data.tick));
                mediaPlayer.setPauseMode(pause);
                mediaPlayer.setMuteMode(this.tile.data.muted);
                break;
        }
    }

    public void setMuteMode(boolean mute) {
        switch (displayMode) {
            case PICTURE:
                break;
            case VIDEO:
            case AUDIO:
                mediaPlayer.setMuteMode(mute);
                break;
        }
    }

    public void release() {
        if (this.isReleased()) {
            return;
        }
        this.released = true;
        imageCache.deuse();
        if (displayMode == Mode.VIDEO || displayMode == Mode.AUDIO) {
            mediaPlayer.release();
            DisplayControl.remove(this);
        }
    }

    public static int limitVolume(int x, int y, int z, int volume, int min, int max) {
        assert Minecraft.getMinecraft().thePlayer != null;
        Vec3 position = Minecraft.getMinecraft().thePlayer.getPosition(WaterFramesMod.proxy.deltaFrames());

        x -= (int) position.xCoord;
        y -= (int) position.yCoord;
        z -= (int) position.zCoord;
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (min > max) {
            int temp = max;
            max = min;
            min = temp;
        }

        if (distance > min)
            volume = (distance > max + 1) ? 0 : (int) (volume * (1 - ((distance - min) / ((1 + max) - min))));
        return (int)(volume * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS));
    }

    public enum Mode {
        VIDEO, PICTURE, AUDIO, CAMERA
    }
}
