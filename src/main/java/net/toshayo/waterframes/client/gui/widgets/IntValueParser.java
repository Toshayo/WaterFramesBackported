package net.toshayo.waterframes.client.gui.widgets;

import net.minecraft.client.resources.I18n;

@FunctionalInterface
public interface IntValueParser {
    IntValueParser NONE = (v, min, max) -> "" + v;
    IntValueParser PIXELS = (v, min, max) -> v + "px";
    IntValueParser PERCENTS = (v, min, max) -> (v * 100) / max + "%";
    IntValueParser BLOCKS = (v, min, max) -> I18n.format("waterframes.gui.blockDistance", v);

    String parse(long value, long minValue, long maxValue);
}

