package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;

import java.util.function.Consumer;

public class Slider extends AbstractWidget {
    protected long minValue, maxValue;
    protected long value;

    protected final IntValueParser parser;
    private final Consumer<Long> onValueChangeListener;

    public Slider(int x, int y, int width, int height, long value, long minValue, long maxValue, IntValueParser parser, Consumer<Long> onValueChangeListener) {
        super(x, y, width, height);

        this.minValue = minValue;
        this.maxValue = maxValue;

        this.parser = parser;

        setValue(value);

        this.onValueChangeListener = onValueChangeListener;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = Math.min(this.maxValue, Math.max(this.minValue, value));
    }

    public void setValueWithNotify(long value) {
        setValue(value);
        onValueChangeListener.accept(getValue());
    }

    public long getMinValue() {
        return minValue;
    }

    public void setMinValue(long minValue) {
        this.minValue = Math.min(minValue, maxValue);
        if(value < minValue) {
            value = minValue;
        }
    }

    public long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = Math.max(minValue, maxValue);
        if(value > maxValue) {
            value = maxValue;
        }
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        renderWidgetRect(ScreenStyles.ITEM_BACKGROUND);

        double step = (width - 2 - 5) / (double)(maxValue - minValue);
        int sliderPosition = (int) ((value - minValue) * step);
        Gui.drawRect(x + 1 + sliderPosition, y + 1, x + 1 + sliderPosition + 5, y + height - 1, ScreenStyles.SCROLL_CURSOR);
        String text = parser.parse(value, minValue, maxValue);
        fontRenderer.drawStringWithShadow(text, x + width / 2 - fontRenderer.getStringWidth(text) / 2, y + fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouseClickMove(mouseX, mouseY, mouseButton, 0);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long clickTime) {
        double step = (width - 2) / (double)(maxValue - minValue);
        long newValue = minValue + (long) ((mouseX - x) / step);
        setValueWithNotify(newValue);
    }
}
