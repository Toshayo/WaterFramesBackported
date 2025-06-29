package net.toshayo.waterframes.client.gui.widgets;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.toshayo.waterframes.client.gui.styles.IconStyles;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class StateButton extends IconButton {
    protected int state;
    protected final IconStyles.Icon[] icons;

    public StateButton(Gui gui, IconStyles.Icon[] icons, int defaultState, int x, int y, int width, int height, Consumer<Button> onClickListener) {
        super(gui, icons[defaultState], x, y, width, height, onClickListener);

        this.icons = icons;
        setState(defaultState);
    }

    public void setState(int state) {
        if(state < 0) {
            state = icons.length - 1;
        }
        if(state >= icons.length) {
            state = 0;
        }
        this.state = state;
        setIcon(icons[this.state]);
    }

    public int getState() {
        return state;
    }

    @Override
    public List<String> getFormattedTooltip() {
        return Arrays.asList(
                I18n.format(icon.tooltipKey + ".1"),
                I18n.format(
                        icon.tooltipKey + ".2",
                        ChatFormatting.AQUA + I18n.format(icon.tooltipKey + ".states." + state)
                )
        );
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isEnabled()) {
            setState(state + 1);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
