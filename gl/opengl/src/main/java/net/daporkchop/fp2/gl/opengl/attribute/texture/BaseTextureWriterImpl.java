/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 DaPorkchop_
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

package net.daporkchop.fp2.gl.opengl.attribute.texture;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.gl.attribute.texture.BaseTextureFormat;
import net.daporkchop.fp2.gl.attribute.texture.BaseTextureWriter;
import net.daporkchop.fp2.gl.opengl.OpenGL;
import net.daporkchop.fp2.gl.opengl.attribute.struct.format.TextureStructFormat;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class BaseTextureWriterImpl<S, F extends BaseTextureFormatImpl<S>, F_EXTERNAL extends BaseTextureFormat<S>> implements BaseTextureWriter<S, F_EXTERNAL> {
    protected final OpenGL gl;
    protected final F formatImpl;
    protected final TextureStructFormat<S> structFormat;

    public BaseTextureWriterImpl(@NonNull F format) {
        this.gl = format.gl();
        this.formatImpl = format;
        this.structFormat = format.structFormat();
    }

    @Override
    public F_EXTERNAL format() {
        return uncheckedCast(this.formatImpl);
    }
}
