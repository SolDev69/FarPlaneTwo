/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.fp2.client.compat;

import lombok.experimental.UtilityClass;
import net.daporkchop.fp2.FP2;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.minecraft.client.settings.GameSettings;

import java.lang.reflect.Method;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class OptifineCompat {
    public static final boolean OF;
    public static final String OF_VERSION;

    static { //copy/pasted from io.github.opencubicchunks.cubicchunks.core.asm.CubicChunksMixinLoader
        String ofVersion = null;
        try {
            Class<?> optifineInstallerClass = Class.forName("optifine.Installer");
            Method getVersionHandler = optifineInstallerClass.getMethod("getOptiFineVersion");
            ofVersion = ((String) getVersionHandler.invoke(null));
            ofVersion = ofVersion.replace("_pre", "");
            ofVersion = ofVersion.substring(ofVersion.length() - 2);

            FP2.LOGGER.info("Detected Optifine version: {}", ofVersion);
        } catch (ClassNotFoundException e) {
            FP2.LOGGER.info("No Optifine detected");
        } catch (Exception e) {
            ofVersion = "E1";
            FP2.LOGGER.error("Optifine detected, but could not detect version. It may not work. Assuming OptifineCompat E1...", e);
        } finally {
            OF_VERSION = ofVersion;
            OF = ofVersion != null;
        }
    }

    public static final int OF_DEFAULT = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "DEFAULT").getInt();
    public static final int OF_FAST = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "FAST").getInt();
    public static final int OF_FANCY = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "FANCY").getInt();
    public static final int OF_OFF = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "OFF").getInt();
    public static final int OF_SMART = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "SMART").getInt();
    public static final int OF_ANIM_ON = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "ANIM_ON").getInt();
    public static final int OF_ANIM_GENERATED = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "ANIM_GENERATED").getInt();
    public static final int OF_ANIM_OFF = !OF ? -1 : PUnsafe.pork_getStaticField(GameSettings.class, "ANIM_OFF").getInt();

    public static final long OF_FOGTYPE_OFFSET = !OF ? -1L : PUnsafe.pork_getOffset(GameSettings.class, "ofFogType");
}