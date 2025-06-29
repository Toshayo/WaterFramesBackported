package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;

import java.util.function.Consumer;

public class SeekbarSlider extends Slider {
    public SeekbarSlider(int x, int y, int width, int height, long value, long maxValue, IntValueParser parser, Consumer<Long> onValueChangeListener) {
        super(x, y, width, height, value, 0, maxValue, parser, onValueChangeListener);
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        renderWidgetRect(0xFF191919);

        double step = (width - 2 - 5) / (double)maxValue;
        Gui.drawRect(x + 1, y + 1, x + 1 + (int)(step * value), y + height - 1, ScreenStyles.ITEM_BACKGROUND);
        String text = parser.parse(value, minValue, maxValue);
        fontRenderer.drawStringWithShadow(text, x + width / 2 - fontRenderer.getStringWidth(text) / 2, y + fontRenderer.FONT_HEIGHT / 2 + 2, 0xFFFFFF);
    }
}
