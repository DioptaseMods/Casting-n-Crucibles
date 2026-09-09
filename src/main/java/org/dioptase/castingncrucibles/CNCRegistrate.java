package org.dioptase.castingncrucibles;

import com.simibubi.create.foundation.data.CreateRegistrate;

public class CNCRegistrate extends CreateRegistrate {
    protected CNCRegistrate(String modid) {
        super(modid);
    }

    public static CNCRegistrate create(String modid) {
        return new CNCRegistrate(modid);
    }
}
