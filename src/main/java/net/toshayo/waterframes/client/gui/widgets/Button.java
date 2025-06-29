package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;

import java.util.function.Consumer;

public class Button extends AbstractWidget {
    private String text;
    private final Consumer<Button> onClickListener;
    private boolean isEnabled;

    public Button(String text, boolean isEnabled, int x, int y, int width, int height, Consumer<Button> onClickListener) {
        super(x, y, width, height);

        this.text = text;
        this.isEnabled = isEnabled;
        this.onClickListener = onClickListener;
    }

    public Button(String text, int x, int y, int width, int height, Consumer<Button> onClickListener) {
        this(text, true, x, y, width, height, onClickListener);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        renderWidgetRect(getBackground(mouseX, mouseY));

        fontRenderer.drawStringWithShadow(text, x + width / 2 - fontRenderer.getStringWidth(text) / 2, y + fontRenderer.FONT_HEIGHT / 2,  isEnabled ? 0xFFFFFF : 0xFF979797);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isEnabled) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            onClickListener.accept(this);
        }
    }

    protected int getBackground(int mouseX, int mouseY) {
        if(isEnabled) {
            return isMouseHovered(mouseX, mouseY) ? ScreenStyles.ITEM_BACKGROUND : ScreenStyles.SCROLL_CURSOR;
        } else {
            return GRAY_DISABLED;
        }
    }
}
