package net.toshayo.waterframes.client.gui.widgets;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.toshayo.waterframes.client.gui.styles.IconStyles;
import net.toshayo.waterframes.types.PositionHorizontal;
import net.toshayo.waterframes.types.PositionVertical;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PositionWidget extends IconButton {
    private PositionHorizontal posX;
    private PositionVertical posY;

    public PositionWidget(Gui gui, int x, int y, PositionHorizontal posX, PositionVertical posY, Consumer<Button> onClickListener) {
        super(gui, IconStyles.POS_BASE, x, y, IconStyles.POS_BASE.width, IconStyles.POS_BASE.height, onClickListener);
        this.posX = posX;
        this.posY = posY;
    }

    public PositionHorizontal getPosX() {
        return posX;
    }

    public PositionVertical getPosY() {
        return posY;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isEnabled()) {
            int areaX = (mouseX - x) * 3 / width;
            int areaY = (mouseY - y) * 3 / height;

            switch (areaX) {
                case 0:
                    posX = PositionHorizontal.LEFT;
                    break;
                case 1:
                    posX = PositionHorizontal.CENTER;
                    break;
                case 2:
                case 3:
                    posX = PositionHorizontal.RIGHT;
                    break;
                default:
                    posX = areaX > 3 ? PositionHorizontal.RIGHT : PositionHorizontal.LEFT;
                    break;
            }

            switch (areaY) {
                case 0:
                    posY = PositionVertical.TOP;
                    break;
                case 1:
                    posY = PositionVertical.CENTER;
                    break;
                case 2:
                case 3:
                    posY = PositionVertical.BOTTOM;
                    break;
                default:
                    posY = areaY > 3 ? PositionVertical.BOTTOM : PositionVertical.TOP;
                    break;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        IconStyles.renderIcon(gui, icon, x, y, mouseX, mouseY, partialTicks);
        int offsetX = posX == PositionHorizontal.LEFT ? 0 : (posX == PositionHorizontal.CENTER ? width / 3 : width * 2 / 3);
        int offsetY = posY == PositionVertical.TOP ? 0 : (posY == PositionVertical.CENTER ? height / 3 : height * 2 / 3);
        IconStyles.renderIcon(gui, IconStyles.POS_ICON, x + offsetX, y + offsetY, mouseX, mouseY, partialTicks);
    }

    @Override
    public List<String> getFormattedTooltip() {
        return Arrays.asList(
                I18n.format(icon.tooltipKey + ".desc"),
                I18n.format(
                        icon.tooltipKey + ".horizontal",
                        ChatFormatting.AQUA + I18n.format(icon.tooltipKey + "." + posX.name().toLowerCase())
                ),
                I18n.format(
                        icon.tooltipKey + ".vertical",
                        ChatFormatting.AQUA + I18n.format(icon.tooltipKey + "." + posY.name().toLowerCase())
                )
        );
    }
}
