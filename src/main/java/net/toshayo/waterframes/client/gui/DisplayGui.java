package net.toshayo.waterframes.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.toshayo.waterframes.DisplayData;
import net.toshayo.waterframes.WFConfig;
import net.toshayo.waterframes.client.gui.styles.IconStyles;
import net.toshayo.waterframes.client.gui.styles.ScreenStyles;
import net.toshayo.waterframes.client.gui.widgets.*;
import net.toshayo.waterframes.network.PacketDispatcher;
import net.toshayo.waterframes.network.packets.SyncPacket;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import net.toshayo.waterframes.types.PositionHorizontal;
import net.toshayo.waterframes.types.PositionVertical;
import org.lwjgl.opengl.GL11;
import toshayopack.team.creative.creativecore.common.util.math.TimeMath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayGui extends GuiScreen {
    protected static final int WIDTH = 270;
    protected static final int HEIGHT = 260;


    private final DisplayTileEntity tile;
    protected int x, y;


    public int alpha, brightness, render_distance, volume, volume_min, volume_max, audioOffset;
    public PositionHorizontal pos_view_x;
    public PositionVertical pos_view_y;
    public float projection_distance, rotation;
    public boolean flip_x, flip_y, show_model, render_behind, lit;

    private final List<AbstractWidget> widgets;
    private GuiTextField urlTbx, widthTbx, heightTbx;
    private Button saveBtn;
    private SeekbarSlider seekbar;
    private IconButton loopButton, playPauseButton;


    public DisplayGui(DisplayTileEntity tile) {
        this.tile = tile;
        widgets = new ArrayList<>();
    }

    @Override
    public void initGui() {
        super.initGui();
        widgets.clear();
        x = (width - WIDTH) / 2;
        y = (height - HEIGHT) / 2;

        rotation = tile.data.rotation;
        alpha = tile.data.alpha;
        brightness = tile.data.brightness;
        render_distance = tile.data.renderDistance;
        projection_distance = tile.data.projectionDistance;
        show_model = tile.isVisible();
        render_behind = tile.data.renderBothSides;
        lit = tile.data.lit;
        flip_x = tile.data.flipX;
        flip_y = tile.data.flipY;
        volume = tile.data.volume;
        volume_min = tile.data.minVolumeDistance;
        volume_max = tile.data.maxVolumeDistance;
        pos_view_x = tile.data.getPosX();
        pos_view_y = tile.data.getPosY();
        audioOffset = tile.data.getAudioPosition().ordinal();

        urlTbx = new GuiTextField(
                fontRendererObj,
                x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 1.5),
                WIDTH - 20, fontRendererObj.FONT_HEIGHT + 4
        );
        urlTbx.setMaxStringLength(255);
        urlTbx.setText(tile.data.uri != null ? tile.data.uri.toString() : "");

        if (tile.caps.resizes()) {
            widthTbx = new GuiTextField(
                    fontRendererObj,
                    x + 10, (int) (y + 10 + fontRendererObj.FONT_HEIGHT * 3.4),
                    WIDTH / 3, 18
            ) {
                @Override
                public void writeText(String text) {
                    super.writeText(
                            text.chars()
                                    .filter(c -> ('0' <= c && c <= '9') || (!getText().contains(".") && c == '.'))
                                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString()
                    );
                }
            };
            widthTbx.setText(String.valueOf(tile.data.getWidth()));

            heightTbx = new GuiTextField(
                    fontRendererObj,
                    x + 10 + 5 + 24 + WIDTH / 3, (int) (y + 10 + fontRendererObj.FONT_HEIGHT * 3.4),
                    WIDTH / 3, 18
            ) {
                @Override
                public void writeText(String text) {
                    super.writeText(
                            text.chars()
                                    .filter(c -> ('0' <= c && c <= '9') || (!getText().contains(".") && c == '.'))
                                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString()
                    );
                }
            };
            heightTbx.setText(String.valueOf(tile.data.getHeight()));

            widgets.add(new IconButton(
                    this, IconStyles.EXPAND_Y,
                    x + 10 + 3 + WIDTH / 3, (int) (y + 10 - 2 + fontRendererObj.FONT_HEIGHT * 3.5),
                    20, 20,
                    button -> {
                        if (tile.display != null) {
                            try {
                                this.heightTbx.setText(String.valueOf(
                                        tile.display.height() / (tile.display.width() / Float.parseFloat(widthTbx.getText()))
                                ));
                            } catch (NumberFormatException ignored) {
                                widthTbx.setText("1");
                            }
                        }
                    }
            ));
            widgets.add(new IconButton(
                    this, IconStyles.EXPAND_X,
                    x + 10 + 8 + 24 + 2 * WIDTH / 3, (int) (y + 10 - 2 + fontRendererObj.FONT_HEIGHT * 3.5),
                    20, 20,
                    button -> {
                        if (tile.display != null) {
                            try {
                                this.widthTbx.setText(String.valueOf(
                                        tile.display.width() / (tile.display.height() / Float.parseFloat(heightTbx.getText()))
                                ));
                            } catch (NumberFormatException ignored) {
                                heightTbx.setText("1");
                            }
                        }
                    }
            ));
            widgets.add(new Slider(
                    x + 10 + 16 + 5, y + 10 + fontRendererObj.FONT_HEIGHT * 7,
                    WIDTH / 2, 16,
                    (long)(rotation * 100), 0, 36000,
                    (value, minValue, maxValue) -> value / 100.0F + "°",
                    value -> rotation = value / 100F
            ));

            widgets.add(new PositionWidget(
                    this,
                    (int)(x + 10 + WIDTH * 0.65), y + 10 + fontRendererObj.FONT_HEIGHT * 7,
                    pos_view_x, pos_view_y,
                    button -> {
                        pos_view_x = ((PositionWidget) button).getPosX();
                        pos_view_y = ((PositionWidget) button).getPosY();
                    }
            ));
        }

        widgets.add(new Slider(
                x + 10 + 16 + 5, y + 10 + fontRendererObj.FONT_HEIGHT * 9,
                WIDTH / 2, 16,
                alpha, 0, 255, IntValueParser.PERCENTS,
                value -> alpha = value.intValue()
        ));
        widgets.add(new Slider(
                x + 10 + 16 + 5, y + 10 + fontRendererObj.FONT_HEIGHT * 11,
                WIDTH / 2, 16,
                brightness, 0, 255, IntValueParser.PERCENTS,
                value -> brightness = value.intValue()
        ));
        widgets.add(new Slider(
                x + 10 + 16 + 5, y + 10 + fontRendererObj.FONT_HEIGHT * 13,
                WIDTH / 2, 16,
                render_distance, 4, WFConfig.maxRenDis(), IntValueParser.BLOCKS,
                value -> render_distance = value.intValue()
        ));

        if (tile.caps.projects()) {
            widgets.add(new Slider(
                    x + 10 + 16 + 5, y + 10 + fontRendererObj.FONT_HEIGHT * 15,
                    WIDTH / 3, 16,
                    (long) (projection_distance * 100), 400, (long) (WFConfig.maxProjDis() * 100),
                    (value, minValue, maxValue) -> I18n.format("waterframes.gui.blockDistance", value / 100F),
                    value -> projection_distance = value / 100F
            ));
            widgets.add(new StateButton(
                    this, new IconStyles.Icon[]{IconStyles.AUDIO_POS_BLOCK, IconStyles.AUDIO_POS_PICTURE, IconStyles.AUDIO_POS_CENTER},
                    audioOffset,
                    x + 10 + 16 + 5 + WIDTH / 2 - IconStyles.AUDIO_POS_BLOCK.width - 4, y + 10 + fontRendererObj.FONT_HEIGHT * 15,
                    IconStyles.AUDIO_POS_BLOCK.width + 4, 20,
                    button -> audioOffset = ((StateButton) button).getState()
            ));
        }

        widgets.add(new Checkbox(
                x + 10 + 24 + WIDTH / 2, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 12.25),
                fontRendererObj.getStringWidth(I18n.format("waterframes.gui.flip_x")) + 12, 11,
                flip_x, "waterframes.gui.flip_x", value -> flip_x = value
        ));
        widgets.add(new Checkbox(
                x + 24 + WIDTH / 2 + WIDTH / 5, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 12.25),
                fontRendererObj.getStringWidth(I18n.format("waterframes.gui.flip_y")) + 12, 11,
                flip_y, "waterframes.gui.flip_y", value -> flip_y = value
        ));

        if (tile.canHideModel()) {
            widgets.add(new Checkbox(
                    x + 10 + 24 + WIDTH / 2, (int) (y + 10 + fontRendererObj.FONT_HEIGHT * 13.75),
                    fontRendererObj.getStringWidth(I18n.format("waterframes.gui.show_model")) + 12, 11,
                    show_model, "waterframes.gui.show_model", value -> show_model = value
            ));
        }
        if (tile.caps.renderBehind()) {
            widgets.add(new Checkbox(
                    x + 10 + 24 + WIDTH / 2, (int) (y + 10 + fontRendererObj.FONT_HEIGHT * 15.25),
                    fontRendererObj.getStringWidth(I18n.format("waterframes.gui.render_behind")) + 12, 11,
                    render_behind, "waterframes.gui.render_behind", value -> render_behind = value
            ));
        }
        widgets.add(new Checkbox(
                x + 10 + 24 + WIDTH / 2, (int) (y + 10 + fontRendererObj.FONT_HEIGHT * 16.75),
                fontRendererObj.getStringWidth(I18n.format("waterframes.gui.lit")) + 12, 11,
                lit, "waterframes.gui.lit", value -> lit = value
        ));

        widgets.add(new Slider(
                x + 10 + 16 + 5, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 18.5),
                2 * WIDTH / 3 + 16, 16,
                volume, 0, WFConfig.maxVol(), IntValueParser.PERCENTS,
                value -> volume = value.intValue()
        ));
        Slider volumeMinSlider = new Slider(
                x + 10 + 16 + 5, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5),
                WIDTH / 3, 16,
                volume_min, 0, Math.min(volume_max, WFConfig.maxVolDis()), IntValueParser.BLOCKS,
                value -> volume_min = value.intValue()
        );
        widgets.add(volumeMinSlider);
        widgets.add(new Slider(
                x + 10 + 16 * 2 + 5 + WIDTH / 3, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5),
                WIDTH / 3, 16,
                volume_max, 0, WFConfig.maxVolDis(), IntValueParser.BLOCKS,
                value -> {
                    volume_max = value.intValue();
                    volumeMinSlider.setMaxValue(volume_max);
                }
        ));
        loopButton = new IconButton(
                this, tile.data.loop ? IconStyles.REPEAT_ON : IconStyles.REPEAT_OFF,
                x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 22.5),
                20, 20,
                button -> tile.loop(!tile.data.loop)
        ) {
            @Override
            public List<String> getFormattedTooltip() {
                return Collections.singletonList(
                        I18n.format(icon.tooltipKey, tile.data.loop ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled")
                );
            }
        };
        widgets.add(loopButton);
        playPauseButton = new IconButton(
                this, tile.data.paused ? IconStyles.PAUSE : IconStyles.PLAY,
                x + 10 + 24, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 22.5),
                20, 20,
                button -> tile.setPause(!tile.data.paused)
        );
        widgets.add(playPauseButton);
        widgets.add(new IconButton(
                this, IconStyles.STOP,
                x + 10 + 24 * 2, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 22.5),
                20, 20,
                button -> tile.setStop()
        ));
        seekbar = new SeekbarSlider(
                x + 10 + 24 * 3, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 22.5),
                2 * WIDTH / 3 - 35, 20,
                tile.data.tick, tile.data.tickMax,
                (value, minValue, maxValue) -> {
                    String current = TimeMath.timestamp((value > maxValue ? (value != 0L && maxValue != 0L ? value % maxValue : 0L) : value) * 50L);
                    return current + "/" + TimeMath.timestamp(maxValue * 50L);
                },
                value -> tile.syncTime(value.intValue(), tile.data.tickMax)
        );
        widgets.add(seekbar);
        saveBtn = new Button(
                I18n.format("waterframes.gui.button.save"),
                x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 25,
                WIDTH / 5, 16,
                button -> PacketDispatcher.wrapper.sendToServer(new SyncPacket(
                        tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord,
                        DisplayData.build(this, tile)
                ))
        );
        widgets.add(saveBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        x = (width - WIDTH) / 2;
        y = (height - HEIGHT) / 2;
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        urlTbx.drawTextBox();

        if (tile.caps.resizes()) {
            widthTbx.drawTextBox();
            heightTbx.drawTextBox();
        }
        widgets.forEach(widget -> widget.render(fontRendererObj, mouseX, mouseY, partialTicks));

        fontRendererObj.drawString(I18n.format("waterframes.gui.label.url"), x + 10, y + 10, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format("waterframes.gui.label.textureSettings"), x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 5.5 + 4), 0xFFFFFF);
        fontRendererObj.drawString(I18n.format("waterframes.gui.label.mediaSettings"), x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 17.5), 0xFFFFFF);

        if (tile.caps.resizes()) {
            IconStyles.renderIcon(this, IconStyles.ROTATION, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 7, mouseX, mouseY, partialTicks);
        }
        IconStyles.renderIcon(this, IconStyles.TRANSPARENCY, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 9, mouseX, mouseY, partialTicks);
        IconStyles.renderIcon(this, IconStyles.BRIGHTNESS, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 11, mouseX, mouseY, partialTicks);
        IconStyles.renderIcon(this, IconStyles.DISTANCE, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 13, mouseX, mouseY, partialTicks);
        if (tile.caps.projects()) {
            IconStyles.renderIcon(this, IconStyles.PROJECTION_DISTANCE, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 15, mouseX, mouseY, partialTicks);
        }
        IconStyles.renderIcon(this, IconStyles.getVolumeIcon(volume, tile.data.muted), x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 18.5), mouseX, mouseY, partialTicks);
        IconStyles.renderIcon(this, IconStyles.VOLUME_RANGE_MIN, x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5), mouseX, mouseY, partialTicks);
        IconStyles.renderIcon(this, IconStyles.VOLUME_RANGE_MAX, x + 10 + 16 + 5 + WIDTH / 3, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5), mouseX, mouseY, partialTicks);

        widgets.stream()
                .filter(widget -> widget instanceof IconButton &&
                        widget.isMouseHovered(mouseX, mouseY) &&
                        !((IconButton) widget).getFormattedTooltip().isEmpty())
                .forEach(widget -> {
                    drawHoveringText(
                            ((IconButton) widget).getFormattedTooltip(),
                            mouseX, mouseY,
                            fontRendererObj
                    );
                    GL11.glDisable(GL11.GL_LIGHTING);
                });
        if (tile.caps.resizes()) {
            renderIconTooltip(IconStyles.ROTATION, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 7, mouseX, mouseY, partialTicks);
        }
        renderIconTooltip(IconStyles.TRANSPARENCY, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 9, mouseX, mouseY, partialTicks);
        renderIconTooltip(IconStyles.BRIGHTNESS, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 11, mouseX, mouseY, partialTicks);
        renderIconTooltip(IconStyles.DISTANCE, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 13, mouseX, mouseY, partialTicks);
        if (tile.caps.projects()) {
            renderIconTooltip(IconStyles.PROJECTION_DISTANCE, x + 10, y + 10 + fontRendererObj.FONT_HEIGHT * 15, mouseX, mouseY, partialTicks);
        }
        renderIconTooltip(IconStyles.getVolumeIcon(volume, tile.data.muted), x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 18.5), mouseX, mouseY, partialTicks);
        renderIconTooltip(IconStyles.VOLUME_RANGE_MIN, x + 10, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5), mouseX, mouseY, partialTicks);
        renderIconTooltip(IconStyles.VOLUME_RANGE_MAX, x + 10 + 16 + 5 + WIDTH / 3, (int)(y + 10 + fontRendererObj.FONT_HEIGHT * 20.5), mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
        drawRect(x - 2, y - 2, x + WIDTH + 2, y + HEIGHT + 2, ScreenStyles.SCREEN_BORDER);
        drawRect(x, y, x + WIDTH, y + HEIGHT, ScreenStyles.SCREEN_BACKGROUND);
    }

    private void renderIconTooltip(IconStyles.Icon icon, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if(mouseX >= x && mouseX <= x + icon.width && mouseY >= y && mouseY <= y + icon.height) {
            drawHoveringText(Collections.singletonList(I18n.format(icon.tooltipKey)), mouseX, mouseY, fontRendererObj);
            GL11.glDisable(GL11.GL_LIGHTING);
        }
    }

    public String getURL() {
        return urlTbx.getText();
    }

    public float getWidth() {
        if(widthTbx == null) {
            return 1;
        }
        try {
            return Float.parseFloat(widthTbx.getText());
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    public float getHeight() {
        if(heightTbx == null) {
            return 1;
        }
        try {
            return Float.parseFloat(heightTbx.getText());
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        widgets.stream()
                .filter(widget -> widget.isMouseHovered(mouseX, mouseY))
                .forEach(widget -> widget.mouseClicked(mouseX, mouseY, mouseButton));
        urlTbx.mouseClicked(mouseX, mouseY, mouseButton);
        if (tile.caps.resizes()) {
            widthTbx.mouseClicked(mouseX, mouseY, mouseButton);
            heightTbx.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long clickTime) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, clickTime);
        widgets.stream()
                .filter(widget -> widget.isMouseHovered(mouseX, mouseY))
                .forEach(widget -> widget.mouseClickMove(mouseX, mouseY, mouseButton, clickTime));
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey) {
        super.keyTyped(eventChar, eventKey);
        urlTbx.textboxKeyTyped(eventChar, eventKey);

        if (tile.caps.resizes()) {
            widthTbx.textboxKeyTyped(eventChar, eventKey);
            heightTbx.textboxKeyTyped(eventChar, eventKey);
        }
    }

    @Override
    public void updateScreen() {
        saveBtn.setEnabled(WFConfig.canSave(Minecraft.getMinecraft().thePlayer, urlTbx.getText()));
        seekbar.setMaxValue(tile.data.tickMax);
        seekbar.setValue(tile.data.tick);
        loopButton.setIcon(tile.data.loop ? IconStyles.REPEAT_ON : IconStyles.REPEAT_OFF);
        playPauseButton.setIcon(tile.data.paused ? IconStyles.PLAY : IconStyles.PAUSE);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
