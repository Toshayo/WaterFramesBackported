package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;

public abstract class AbstractWidget {
    protected final static int GRAY_DISABLED = 0xFF3D3D3D;

    protected final int x, y, width, height;

    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isMouseHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public abstract void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks);

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long clickTime) {

    }

    protected void renderWidgetRect(int bg) {
        Gui.drawRect(x, y, x + width, y + height, getBorderColor());
        Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, bg);
    }

    protected int getBorderColor() {
        return 0xFF000000;
    }
}
