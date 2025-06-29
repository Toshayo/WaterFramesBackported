package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.toshayo.waterframes.client.gui.styles.IconStyles;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class IconButton extends Button {
    protected IconStyles.Icon icon;
    protected final Gui gui;

    public IconButton(Gui gui, IconStyles.Icon icon, int x, int y, int width, int height, Consumer<Button> onClickListener) {
        super("", x, y, width, height, onClickListener);

        this.gui = gui;
        this.icon = icon;
    }

    public void setIcon(IconStyles.Icon icon) {
        this.icon = icon;
    }

    public IconStyles.Icon getIcon() {
        return icon;
    }

    public List<String> getFormattedTooltip() {
        if(icon.tooltipKey.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(I18n.format(icon.tooltipKey));
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks) {
        renderWidgetRect(getBackground(mouseX, mouseY));
        IconStyles.renderIcon(gui, icon, x + 2, y + 2, mouseX, mouseY, partialTicks);
    }
}
