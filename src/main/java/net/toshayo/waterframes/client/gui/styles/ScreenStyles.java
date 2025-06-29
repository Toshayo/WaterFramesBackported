package net.toshayo.waterframes.client.gui.styles;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScreenStyles {

    public static final int BLUE_BORDER = color(72, 220, 219, 1.0F);
    public static final int BLUE_BACKGROUND = color(47, 137, 137, 1.0F);
    public static final int BLUE_HOVER_BACKGROUND = color(67, 157, 157, 1.0F);
    public static final int DARK_BLUE_BACKGROUND = color(27, 36, 52, 1);

    public static final int DARK_BLUE_BACKGROUND_DISABLED = color(27, 36, 52, 0.5f);
    public static final int DARK_BLUE_HIGHLIGHT = color(27 * 2, 36 * 2, 52 * 2, 1);

    public static final int RED_BORDER = color(255, 60, 60, 1.0F);
    public static final int RED_BACKGROUND = color(140, 56, 52, 1);
    public static final int RED_HOVER_BACKGROUND = color(160, 76, 72, 1);

    public static final int SCREEN_BACKGROUND = color(37, 50, 72, 1.0F);
    public static final int SCREEN_BORDER = color(23, 30, 42, 1.0F);
    public static final int ITEM_BACKGROUND = 0xFF7F7F7F;
    public static final int SCROLL_CURSOR = 0xFF666666;

    public static int color(int red, int green, int blue, float alpha) {
        return ((int)(alpha * 0xFF) << 24) + (red << 16) + (green << 8) + blue;
    }

    public static int color(float red, float green, float blue, float alpha) {
        return ((int)(alpha * 0xFF) << 24) + ((int)(red * 0xFF) << 16) + ((int)(green * 0xFF) << 8) + (int)(blue * 0xFF);
    }
}