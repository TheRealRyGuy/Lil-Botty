package me.ryguy.ctfbot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EmbedBuilder {
    /**
     * TODO: Actually finish this and apply this to a MessageUtil class
     * This only functions as a holder for fields for SS commands rn
     */
    List<Field> fields;

    public EmbedBuilder() {
        fields = new ArrayList<>();
    }

    public void addField(Field f) {
        fields.add(f);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Field {
        String title;
        String value;
        boolean inline;
    }
}
