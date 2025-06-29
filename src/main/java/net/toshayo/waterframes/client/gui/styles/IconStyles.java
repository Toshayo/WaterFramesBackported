package net.toshayo.waterframes.client.gui.styles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.toshayo.waterframes.WaterFramesMod;
import org.lwjgl.opengl.GL11;

public class IconStyles {
    public static final ResourceLocation location = new ResourceLocation(WaterFramesMod.MOD_ID, "textures/screen_atlas.png");

    // VOLUME COLUM (chunk 0)
    public static final Icon VOLUME = create("waterframes.gui.icon.volume", 0, 0);
    public static final Icon VOLUME_MUTED = create("waterframes.gui.icon.volume",0, 1);
    public static final Icon VOLUME_1 = create("waterframes.gui.icon.volume", 0, 2);
    public static final Icon VOLUME_2 = create("waterframes.gui.icon.volume", 0, 3);
    public static final Icon VOLUME_3 = create("waterframes.gui.icon.volume", 0, 4);
    public static final Icon VOLUME_OVERFLOW = create("waterframes.gui.icon.volume", 0, 5);
    public static final Icon VOLUME_RANGE_MIN = create("waterframes.gui.icon.volumeBlockMin", 0, 6);
    public static final Icon VOLUME_RANGE_MAX = create("waterframes.gui.icon.volumeBlockMax", 0, 7);
    public static final Icon AUDIO_POS_BLOCK = create("waterframes.gui.audio_pos", 0, 13, 2);
    public static final Icon AUDIO_POS_CENTER = create("waterframes.gui.audio_pos", 0, 14, 2);
    public static final Icon AUDIO_POS_PICTURE = create("waterframes.gui.audio_pos", 0, 15, 2);


    // ICON COLUM (chunk 1)
    public static final Icon EXPAND_X = create("waterframes.gui.resizeX", 1, 0);
    public static final Icon EXPAND_Y = create("waterframes.gui.resizeY", 1, 1);
    public static final Icon ROTATION = create("waterframes.gui.icon.rotation", 1, 2);
    public static final Icon TRANSPARENCY = create("waterframes.gui.icon.alpha", 1, 3);
    public static final Icon BRIGHTNESS = create("waterframes.gui.icon.brightness", 1, 4);
    public static final Icon REPEAT_ON = create("waterframes.gui.button.loop", 1, 5);
    public static final Icon REPEAT_OFF = create("waterframes.gui.button.loop", 1, 6);
    public static final Icon DISTANCE = create("waterframes.gui.icon.maxRendering", 1, 7);
    public static final Icon PROJECTION_DISTANCE = create("waterframes.gui.icon.projectionDistance", 1, 8);

    // STATUS ICONS (chunk 2)
    public static final Icon STATUS_OK = create("waterframes.gui.status", 2, 0);
    public static final Icon STATUS_WARN = create("waterframes.gui.status", 2, 1);
    public static final Icon STATUS_IDLE = create("waterframes.gui.status", 2, 2);
    public static final Icon STATUS_ERROR = create("waterframes.gui.status", 2, 3);
    public static final Icon STATUS_HACKED = create("waterframes.gui.status", 2, 4);
    public static final Icon STATUS_PEM = create("waterframes.gui.status", 2, 5);
    public static final Icon STATUS_AFFECTED = create("waterframes.gui.status", 2, 6);
    public static final Icon STATUS_CASSETE_MODE = create("waterframes.gui.status", 2, 7);
    public static final Icon STATUS_OFF = create("waterframes.gui.status", 2, 8);

    // POSITION ICONS (chunk 3)
    // POS ICON
    public static final Icon POS_ICON = create(4, 12);
    public static final Icon POS_BASE = create("waterframes.gui.position", 4, 13, 3, 3);

    // ACTION COLUM (chunk 15)
    public static final Icon PLAY = create(15, 0);
    public static final Icon PAUSE = create(15, 1);
    public static final Icon STOP = create(15, 2);
    public static final Icon FAST_FOWARD = create(15, 3);
    public static final Icon FAST_BACKWARD = create(15, 4);
    public static final Icon NEXT_MEDIA = create(15, 5);
    public static final Icon BACK_MEDIA = create(15, 6);
    public static final Icon ADD = create(15, 7);
    public static final Icon OFF_ON = create(15, 8);
    public static final Icon MUTE = create(15, 9);
    public static final Icon RELOAD = create(15, 10);
    public static final Icon VOLUME_DOWN = create(15, 11);
    public static final Icon VOLUME_UP = create(15, 12);
    public static final Icon CHANNEL_UP = create(15, 13);
    public static final Icon CHANNEL_DOWN = create(15, 14);

    private static Icon create(int chunkX, int chunkY) {
        return create("", chunkX, chunkY, 1);
    }

    private static Icon create(String tooltipKey, int chunkX, int chunkY) {
        return create(tooltipKey, chunkX, chunkY, 1);
    }

    private static Icon create(String tooltipKey, int chunkX, int chunkY, int chunkXMultiplier) {
        return create(tooltipKey, chunkX, chunkY, chunkXMultiplier, 1);
    }

    private static Icon create(String tooltipKey, int chunkX, int chunkY, int chunkXMultiplier, int chunkYMultiplier) {
        return new Icon(tooltipKey, location, 16 * chunkX, 16 * chunkY, 16 * chunkXMultiplier, 16 * chunkYMultiplier);
    }

    public static Icon getVolumeIcon(int volume, boolean muted) {
        if (muted) return MUTE;
        if (volume > 100) {
            return IconStyles.VOLUME_OVERFLOW;
        } else if (volume >= 90){
            return IconStyles.VOLUME;
        } else if (volume >= 65) {
            return IconStyles.VOLUME_3;
        } else if (volume >= 35) {
            return IconStyles.VOLUME_2;
        } else if (volume >= 1) {
            return IconStyles.VOLUME_1;
        } else {
            return IconStyles.VOLUME_MUTED;
        }
    }

    public static class Icon {
        public final String tooltipKey;
        public final ResourceLocation texture;
        public final int u, v, width, height;

        public Icon(String tooltipKey, ResourceLocation texture, int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;

            this.tooltipKey = tooltipKey;
            this.texture = texture;
        }

        public Icon(String tooltipKey, ResourceLocation texture, int u, int v) {
            this(tooltipKey, texture, u, v, 16, 16);
        }
    }

    public static void renderIcon(Gui gui, IconStyles.Icon icon, int x, int y, int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1, 1,1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon.texture);
        gui.drawTexturedModalRect(x, y, icon.u, icon.v, icon.width, icon.height);
    }
}