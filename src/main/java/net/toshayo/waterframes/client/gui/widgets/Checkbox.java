package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;

import java.util.function.Consumer;

public class Checkbox extends AbstractWidget {
    protected boolean isChecked;
    private final Consumer<Boolean> onValueChangeListener;
    private String text;

    public Checkbox(int x, int y, int width, int height, boolean isChecked, String translationKey, Consumer<Boolean> onValueChangeListener) {
        super(x, y, width, height);

        this.isChecked = isChecked;
        this.onValueChangeListener = onValueChangeListener;
        this.text = I18n.format(translationKey);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(x + 3, y + 4, x + 3 + 7, y + 4 + 7, 0xFF000000);
        Gui.drawRect(x + 4, y + 5, x + 4 + 7 - 2, y + 5 + 7 - 2, ScreenStyles.ITEM_BACKGROUND);
        if(isChecked) {
            fontRenderer.drawString("x", x + 4, y + 3, 0xFFFFFF);
        }
        fontRenderer.drawStringWithShadow(text, x + 12, y + fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        isChecked = !isChecked;
        onValueChangeListener.accept(isChecked);
    }
}
