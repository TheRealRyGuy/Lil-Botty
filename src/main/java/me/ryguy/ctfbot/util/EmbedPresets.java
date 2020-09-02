package me.ryguy.ctfbot.util;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class EmbedPresets {
    public static EmbedCreateSpec success() {
        return new EmbedCreateSpec().setColor(Color.GREEN);
    }
    public static EmbedCreateSpec error() {
        return new EmbedCreateSpec().setColor(Color.RED);
    }
    public static EmbedCreateSpec info() {
        return new EmbedCreateSpec().setColor(Color.TAHITI_GOLD);
    }
}
