package org.dioptase.castingncrucibles.registry;

import net.createmod.catnip.lang.Lang;

import java.util.Locale;

public class CNCLang extends Lang {
    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
