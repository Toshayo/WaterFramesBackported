package net.toshayo.waterframes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.toshayo.waterframes.client.gui.DisplayGui;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import net.toshayo.waterframes.types.AudioPosition;
import net.toshayo.waterframes.types.PositionHorizontal;
import net.toshayo.waterframes.types.PositionVertical;
import toshayopack.team.creative.creativecore.common.util.math.Vec2f;

import java.net.URI;
import java.util.UUID;

public class DisplayData {
    public static final String URL = "url";
    public static final String PLAYER_UUID = "player_uuid";
    public static final String ACTIVE = "active";
    public static final String MIN_X = "min_x";
    public static final String MIN_Y = "min_y";
    public static final String MAX_X = "max_x";
    public static final String MAX_Y = "max_y";

    public static final String FLIP_X = "flip_x";
    public static final String FLIP_Y = "flip_y";

    public static final String ROTATION = "rotation";
    public static final String ALPHA = "alpha";
    public static final String BRIGHTNESS = "brightness";
    public static final String RENDER_DISTANCE = "render_distance";

    public static final String VOLUME = "volume";
    public static final String VOL_RANGE_MIN = "volume_min_range";
    public static final String VOL_RANGE_MAX = "volume_max_range";
    public static final String LOOP = "loop";
    public static final String PAUSED = "paused";
    public static final String MUTED = "muted";
    public static final String TICK = "tick";
    public static final String TICK_MAX = "tick_max";
    public static final String DATA_V = "data_v";

    // FRAME KEYS
    public static final String RENDER_BOTH_SIDES = "render_both";

    // PROJECTOR
    public static final String PROJECTION_DISTANCE = "projection_distance";
    public static final String AUDIO_OFFSET = "audio_offset";

    public static final short V = 2;

    public static final UUID NIL_UUID = new UUID(0, 0);

    public URI uri = null;
    public UUID uuid = NIL_UUID;
    public boolean active = true;
    public final Vec2f min = new Vec2f(0f, 0f);
    public final Vec2f max = new Vec2f(1f, 1f);

    public boolean flipX = false;
    public boolean flipY = false;

    public float rotation = 0;
    public int alpha = 255;
    public int brightness = 255;
    public int renderDistance = WFConfig.maxRenDis(32);

    public int volume = WFConfig.maxVol();
    public int maxVolumeDistance = WFConfig.maxVolDis(20);
    public int minVolumeDistance = Math.min(5, maxVolumeDistance);

    public boolean loop = true;
    public boolean paused = false;
    public boolean muted = false;
    public int tick = 0;
    public int tickMax = -1;

    public boolean renderBothSides = false;

    // PROJECTOR VALUES
    public float projectionDistance = WFConfig.maxProjDis(8f);
    public float audioOffset = 0;

    public boolean isUriInvalid() {
        return this.uri == null;
    }

    public PositionHorizontal getPosX() {
        return this.min.x == 0 ? PositionHorizontal.LEFT : this.max.x == 1 ? PositionHorizontal.RIGHT : PositionHorizontal.CENTER;
    }

    public PositionVertical getPosY() {
        return this.min.y == 0 ? PositionVertical.TOP : this.max.y == 1 ? PositionVertical.BOTTOM : PositionVertical.CENTER;
    }

    public float getWidth() {
        return this.max.x - this.min.x;
    }

    public float getHeight() {
        return this.max.y - this.min.y;
    }

    public void save(NBTTagCompound nbt, DisplayTileEntity tile) {
        nbt.setString(URL, isUriInvalid() ? "" : uri.toString());
        nbt.setString(PLAYER_UUID, uuid.toString());
        nbt.setBoolean(ACTIVE, active);
        if (tile.caps.resizes()) {
            nbt.setFloat(MIN_X, min.x);
            nbt.setFloat(MIN_Y, min.y);
            nbt.setFloat(MAX_X, max.x);
            nbt.setFloat(MAX_Y, max.y);
            nbt.setFloat(ROTATION, rotation);
        }
        nbt.setInteger(RENDER_DISTANCE, renderDistance);
        nbt.setBoolean(FLIP_X, flipX);
        nbt.setBoolean(FLIP_Y, flipY);
        nbt.setInteger(ALPHA, alpha);
        nbt.setInteger(BRIGHTNESS, brightness);
        nbt.setInteger(VOLUME, volume);
        nbt.setInteger(VOL_RANGE_MIN, minVolumeDistance);
        nbt.setInteger(VOL_RANGE_MAX, maxVolumeDistance);
        nbt.setBoolean(PAUSED, paused);
        nbt.setBoolean(MUTED, muted);
        nbt.setLong(TICK, tick);
        nbt.setLong(TICK_MAX, tickMax);
        nbt.setBoolean(LOOP, loop);

        if (tile.caps.renderBehind()) {
            nbt.setBoolean(RENDER_BOTH_SIDES, renderBothSides);
        }

        if (tile.caps.projects()) {
            nbt.setFloat(PROJECTION_DISTANCE, projectionDistance);
            nbt.setFloat(AUDIO_OFFSET, audioOffset);
        }

        nbt.setShort(DATA_V, V);
    }

    public void load(NBTTagCompound nbt, DisplayTileEntity tile) {
        String url = nbt.getString(URL);
        this.uri = url.isEmpty() ? null : WaterFramesMod.createURI(url);
        this.uuid = nbt.hasKey(PLAYER_UUID) ? UUID.fromString(nbt.getString(PLAYER_UUID)) : this.uuid;
        this.active = nbt.hasKey(ACTIVE) ? nbt.getBoolean(ACTIVE) : this.active;
        if (tile.caps.resizes()) {
            this.min.x = nbt.getFloat(MIN_X);
            this.min.y = nbt.getFloat(MIN_Y);
            this.max.x = nbt.getFloat(MAX_X);
            this.max.y = nbt.getFloat(MAX_Y);
            this.rotation = nbt.getFloat(ROTATION);
        }
        this.renderDistance = WFConfig.maxRenDis(nbt.getInteger(RENDER_DISTANCE));
        this.flipX = nbt.getBoolean(FLIP_X);
        this.flipY = nbt.getBoolean(FLIP_Y);
        this.alpha = nbt.hasKey(ALPHA) ? nbt.getInteger(ALPHA) : this.alpha;
        this.brightness = nbt.hasKey(BRIGHTNESS) ? nbt.getInteger(BRIGHTNESS) : this.alpha;
        this.volume = nbt.hasKey(VOLUME) ? WFConfig.maxVol(nbt.getInteger(VOLUME)) : this.volume;
        this.maxVolumeDistance = nbt.hasKey(VOL_RANGE_MAX) ? WFConfig.maxVolDis(nbt.getInteger(VOL_RANGE_MAX)) : this.maxVolumeDistance;
        this.minVolumeDistance = nbt.hasKey(VOL_RANGE_MIN) ? Math.min(nbt.getInteger(VOL_RANGE_MIN), this.maxVolumeDistance) : this.minVolumeDistance;
        this.paused = nbt.getBoolean(PAUSED);
        this.muted = nbt.getBoolean(MUTED);
        this.tick = nbt.getInteger(TICK);
        this.tickMax = nbt.hasKey(TICK_MAX) ? nbt.getInteger(TICK_MAX) : this.tickMax;
        this.loop = nbt.getBoolean(LOOP);

        if (tile.caps.renderBehind()) {
            this.renderBothSides = nbt.getBoolean(RENDER_BOTH_SIDES);
        }

        if (tile.caps.projects()) {
            this.projectionDistance = nbt.hasKey(PROJECTION_DISTANCE) ? WFConfig.maxProjDis(nbt.getInteger(PROJECTION_DISTANCE)) : this.projectionDistance;
            this.audioOffset = nbt.hasKey(AUDIO_OFFSET) ? nbt.getFloat(AUDIO_OFFSET) : this.audioOffset;
        }

        if (nbt.getShort(DATA_V) == 1) {
            this.alpha = (int) (nbt.getFloat(ALPHA) * 255);
            this.brightness = (int) (nbt.getFloat(BRIGHTNESS) * 255);
        } else { // NO EXISTS
            if (!nbt.hasKey("maxx")) return; // no exists then ignore, prevents broke new data on 2.0
            this.min.x = nbt.getFloat("minx");
            this.min.y = nbt.getFloat("miny");
            this.max.x = nbt.getFloat("maxx");
            this.max.y = nbt.getFloat("maxy");

            this.flipX = nbt.getBoolean("flipX");
            this.flipY = nbt.getBoolean("flipY");

            this.maxVolumeDistance = WFConfig.maxVolDis((int) nbt.getFloat("max"));
            this.minVolumeDistance = Math.min((int) nbt.getFloat("min"), maxVolumeDistance);

            this.renderDistance = nbt.getInteger("render");

            if (tile.canHideModel()) {
                tile.setVisibility(nbt.getBoolean("visibleFrame"));
            }

            if (tile.caps.renderBehind()) {
                this.renderBothSides = nbt.getBoolean("bothSides");
            }
        }

        this.restrictWidth();
        this.restrictHeight();
    }

    public void setAudioPosition(AudioPosition position) {
        switch (position) {
            case BLOCK:
                this.audioOffset = 0f;
                break;
            case PROJECTION:
                this.audioOffset = projectionDistance;
                break;
            case CENTER:
                this.audioOffset = projectionDistance / 2f;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public AudioPosition getAudioPosition() {
        return audioOffset == 0 ? AudioPosition.BLOCK : audioOffset == projectionDistance ? AudioPosition.PROJECTION : AudioPosition.CENTER;
    }

    public void setWidth(final float width) {
        this.setWidth(this.getPosX(), width);
    }

    public void setWidth(final PositionHorizontal position, final float width) {
        switch (position) {
            case LEFT:
                this.min.x = 0;
                this.max.x = width;
                break;
            case RIGHT:
                this.min.x = 1 - width;
                this.max.x = 1;
                break;
            default:
                float middle = width / 2;
                this.min.x = 0.5F - middle;
                this.max.x = 0.5F + middle;
                break;
        }
    }

    public void setHeight(final float height) {
        this.setHeight(this.getPosY(), height);
    }

    public void setHeight(final PositionVertical position, final float height) {
        switch (position) {
            case TOP:
                this.min.y = 0;
                this.max.y = height;
                break;
            case BOTTOM:
                this.min.y = 1 - height;
                this.max.y = 1;
                break;
            default:
                float middle = height / 2;
                this.min.y = 0.5F - middle;
                this.max.y = 0.5F + middle;
                break;
        }
    }

    private void restrictWidth() {
        float maxWidth = WFConfig.maxWidth();
        if (getWidth() > maxWidth) {
            switch (getPosX()) {
                case LEFT:
                    this.min.x = 0;
                    this.max.x = maxWidth;
                    break;
                case RIGHT:
                    this.min.x = 1 - maxWidth;
                    this.max.x = 1;
                    break;
                default:
                    float middle = maxWidth / 2f;
                    this.min.x = 0.5F - middle;
                    this.max.x = 0.5F + middle;
                    break;
            }
        }
    }

    public DisplayData setProjectionDistance(float projectionDistance) {
        this.projectionDistance = projectionDistance;
        return this;
    }

    private void restrictHeight() {
        float maxHeight = WFConfig.maxHeight();
        if (getHeight() > maxHeight) {
            switch (getPosY()) {
                case TOP:
                    this.min.y = 0f;
                    this.max.y = maxHeight;
                    break;
                case BOTTOM:
                    this.min.y = 1f - maxHeight;
                    this.max.y = 1f;
                    break;
                default:
                    float middle = maxHeight / 2f;
                    this.min.y = 0.5F - middle;
                    this.max.y = 0.5F + middle;
                    break;
            }
        }
    }

    public static NBTTagCompound build(DisplayGui screen, DisplayTileEntity tile) {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString(URL, screen.getURL());
        nbt.setBoolean(ACTIVE, true); // reset

        if (tile.caps.resizes()) {
            nbt.setFloat("width", Math.max(0.1F, screen.getWidth()));
            nbt.setFloat("height", Math.max(0.1F, screen.getHeight()));
            nbt.setInteger("pos_x",  screen.pos_view_x.ordinal());
            nbt.setInteger("pos_y", screen.pos_view_y.ordinal());
            nbt.setFloat(ROTATION, screen.rotation);
        }

        nbt.setBoolean(FLIP_X, screen.flip_x);
        nbt.setBoolean(FLIP_Y, screen.flip_y);

        nbt.setInteger(ALPHA, screen.alpha);
        nbt.setInteger(BRIGHTNESS, screen.brightness);
        nbt.setInteger(RENDER_DISTANCE, screen.render_distance);

        nbt.setInteger(VOLUME, screen.volume);
        nbt.setInteger(VOL_RANGE_MIN, screen.volume_min);
        nbt.setInteger(VOL_RANGE_MAX, screen.volume_max);

        if (tile.canHideModel()) {
            nbt.setBoolean("visible", screen.show_model);
        }

        if (tile.caps.renderBehind()) {
            nbt.setBoolean(RENDER_BOTH_SIDES, screen.render_behind);
        }

        if (tile.caps.projects()) {
            nbt.setFloat(PROJECTION_DISTANCE, screen.projection_distance);
            nbt.setInteger(AUDIO_OFFSET, screen.audioOffset);
        }

        return nbt;
    }

    public static void sync(DisplayTileEntity tile, EntityPlayer player, NBTTagCompound nbt) {
        String url = nbt.getString(URL);
        if (WFConfig.canSave(player, url)) {
            final URI uri = WaterFramesMod.createURI(url);
            if (tile.data.isUriInvalid() || !tile.data.uri.equals(uri)) {
                tile.data.tick = 0;
                tile.data.tickMax = -1;
            }
            tile.data.uri = uri;
            tile.data.uuid = tile.data.isUriInvalid() ? player.getUniqueID() : new UUID(0, 0);
            tile.data.active = nbt.getBoolean(ACTIVE);

            if (tile.caps.resizes()) {
                float width = WFConfig.maxWidth(nbt.getFloat("width"));
                float height = WFConfig.maxHeight(nbt.getFloat("height"));
                int posX = nbt.getInteger("pos_x");
                int posY = nbt.getInteger("pos_y");

                tile.data.setWidth(PositionHorizontal.VALUES[posX], width);
                tile.data.setHeight(PositionVertical.VALUES[posY], height);
                tile.data.rotation = nbt.getFloat(ROTATION);
            }

            tile.data.flipX = nbt.getBoolean(FLIP_X);
            tile.data.flipY = nbt.getBoolean(FLIP_Y);
            tile.data.alpha = nbt.getInteger(ALPHA);
            tile.data.brightness = nbt.getInteger(BRIGHTNESS);
            tile.data.renderDistance = WFConfig.maxRenDis(nbt.getInteger(RENDER_DISTANCE));
            tile.data.volume = WFConfig.maxVol(nbt.getInteger(VOLUME));
            tile.data.maxVolumeDistance = WFConfig.maxVolDis(nbt.getInteger(VOL_RANGE_MAX));
            tile.data.minVolumeDistance = Math.min(nbt.getInteger(VOL_RANGE_MIN), tile.data.maxVolumeDistance);
            if (tile.data.minVolumeDistance > tile.data.maxVolumeDistance) {
                tile.data.maxVolumeDistance = tile.data.minVolumeDistance;
            }

            if (tile.canHideModel()) {
                tile.setVisibility(nbt.getBoolean("visible"));
            }

            if (tile.caps.renderBehind()) {
                tile.data.renderBothSides = nbt.getBoolean(RENDER_BOTH_SIDES);
            }

            if (tile.caps.projects()) {
                int mode = nbt.getInteger(AUDIO_OFFSET);

                tile.data.projectionDistance = WFConfig.maxProjDis(nbt.getFloat(PROJECTION_DISTANCE));
                tile.data.setAudioPosition(AudioPosition.VALUES[mode]);
            }
        }

        tile.markDirty();
    }
}