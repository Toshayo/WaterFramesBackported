package net.toshayo.waterframes.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.toshayo.waterframes.client.gui.styles.IconStyles;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;
import net.toshayo.waterframes.client.gui.widgets.AbstractWidget;
import net.toshayo.waterframes.client.gui.widgets.IconButton;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;

import java.util.ArrayList;
import java.util.List;

public class RemoteControlGui extends GuiScreen {
    protected static final int WIDTH = 82;
    protected static final int HEIGHT = 150;

    private final DisplayTileEntity tile;
    protected int x, y;

    private final List<AbstractWidget> widgets;

    public RemoteControlGui(DisplayTileEntity tile) {
        this.tile = tile;

        widgets = new ArrayList<>();
    }

    @Override
    public void initGui() {
        super.initGui();
        widgets.clear();

        x = (width - WIDTH) / 2;
        y = (height - HEIGHT) / 2;

        widgets.add(new IconButton(
                this, IconStyles.OFF_ON,
                x + 10, y + 10, 20, 20,
                button -> tile.setActive(!tile.data.active)
        ) {
            @Override
            protected int getBackground(int mouseX, int mouseY) {
                return isMouseHovered(mouseX, mouseY) ? ScreenStyles.RED_HOVER_BACKGROUND : ScreenStyles.RED_BACKGROUND;
            }

            @Override
            protected int getBorderColor() {
                return ScreenStyles.RED_BORDER;
            }
        });

        widgets.add(new IconButton(
                this, IconStyles.MUTE,
                x + WIDTH - 30, y + 10, 20, 20,
                button -> tile.setMute(!tile.data.muted)
        ) {
            @Override
            protected int getBackground(int mouseX, int mouseY) {
                return isMouseHovered(mouseX, mouseY) ? ScreenStyles.BLUE_HOVER_BACKGROUND : ScreenStyles.BLUE_BACKGROUND;
            }

            @Override
            protected int getBorderColor() {
                return ScreenStyles.BLUE_BORDER;
            }
        });

        widgets.add(new IconButton(
                this, IconStyles.VOLUME_UP,
                x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 4, 20, 20,
                button -> tile.volumeUp()
        ));

        IconButton channelUp = new IconButton(
                this, IconStyles.CHANNEL_UP,
                x + WIDTH - 30, y + 10 + fontRendererObj.FONT_HEIGHT * 4, 20, 20,
                button -> tile.prevUri()
        );
        channelUp.setEnabled(!this.tile.data.uris.isEmpty());
        widgets.add(channelUp);

        widgets.add(new IconButton(
                this, IconStyles.VOLUME_DOWN,
                x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 6.5), 20, 20,
                button -> tile.volumeDown()
        ));

        widgets.add(new IconButton(
                this, IconStyles.RELOAD,
                x + WIDTH / 2 - 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 6.5), 20, 20,
                button -> {
                    if(tile.imageCache != null) tile.imageCache.reload();
                }
        ));

        IconButton channelDown = new IconButton(
                this, IconStyles.CHANNEL_DOWN,
                x + WIDTH - 30, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 6.5), 20, 20,
                button -> tile.nextUri()
        );
        channelDown.setEnabled(!this.tile.data.uris.isEmpty());
        widgets.add(channelDown);

        widgets.add(new IconButton(
                this, IconStyles.PLAY,
                x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 10, 20, 20,
                button -> tile.setPause(false)
        ));

        widgets.add(new IconButton(
                this, IconStyles.PAUSE,
                x + WIDTH / 2 - 10, y + 10 + fontRendererObj.FONT_HEIGHT * 10, 20, 20,
                button -> tile.setPause(true)
        ));

        widgets.add(new IconButton(
                this, IconStyles.STOP,
                x + WIDTH - 30, y + 10 + fontRendererObj.FONT_HEIGHT * 10, 20, 20,
                button -> tile.setStop()
        ));

        widgets.add(new IconButton(
                this, IconStyles.FAST_BACKWARD,
                x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 12.5), 20, 20,
                button -> tile.rewind()
        ));

        widgets.add(new IconButton(
                this, IconStyles.FAST_FOWARD,
                x + WIDTH - 30, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 12.5), 20, 20,
                button -> tile.fastFoward()
        ));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        x = (width - WIDTH) / 2;
        y = (height - HEIGHT) / 2;
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        widgets.forEach(widget -> widget.render(fontRendererObj, mouseX, mouseY, partialTicks));
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
        drawRect(x - 2, y - 2, x + WIDTH + 2, y + HEIGHT + 2, ScreenStyles.SCREEN_BORDER);
        drawRect(x, y, x + WIDTH, y + HEIGHT, ScreenStyles.SCREEN_BACKGROUND);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        widgets.stream()
                .filter(widget -> widget.isMouseHovered(mouseX, mouseY))
                .forEach(widget -> widget.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
