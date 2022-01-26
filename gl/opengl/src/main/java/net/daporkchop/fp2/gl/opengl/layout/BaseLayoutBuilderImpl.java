/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 DaPorkchop_
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

package net.daporkchop.fp2.gl.opengl.layout;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.fp2.gl.attribute.AttributeFormat;
import net.daporkchop.fp2.gl.attribute.AttributeUsage;
import net.daporkchop.fp2.gl.attribute.texture.TextureFormat2D;
import net.daporkchop.fp2.gl.layout.BaseLayout;
import net.daporkchop.fp2.gl.layout.BaseLayoutBuilder;
import net.daporkchop.fp2.gl.opengl.OpenGL;
import net.daporkchop.fp2.gl.opengl.attribute.BaseAttributeFormatImpl;
import net.daporkchop.fp2.gl.opengl.attribute.InternalAttributeUsage;
import net.daporkchop.fp2.gl.opengl.attribute.common.AttributeFormatImpl;
import net.daporkchop.fp2.gl.opengl.attribute.texture.BaseTextureFormatImpl;

import java.util.Set;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class BaseLayoutBuilderImpl<BUILDER extends BaseLayoutBuilder<BUILDER, L>, L extends BaseLayout> implements BaseLayoutBuilder<BUILDER, L> {
    @NonNull
    protected final OpenGL gl;

    protected final ImmutableMap.Builder<BaseAttributeFormatImpl<?>, InternalAttributeUsage> formatsUsages = ImmutableMap.builder();

    protected void with(@NonNull BaseAttributeFormatImpl<?> format, @NonNull InternalAttributeUsage usage) {
        checkArg(this.validUsages().contains(usage), "%s doesn't support %s", usage);
        this.formatsUsages.put(format, usage);
    }

    protected abstract Set<InternalAttributeUsage> validUsages();

    @Override
    public BUILDER with(@NonNull AttributeUsage usage, @NonNull AttributeFormat<?> format) {
        checkArg(format.usage().contains(usage), "%s doesn't support %s", format, usage);
        this.with((AttributeFormatImpl<?, ?>) format, InternalAttributeUsage.fromExternal(usage));
        return uncheckedCast(this);
    }

    @Override
    public BUILDER withTexture(@NonNull TextureFormat2D<?> format) {
        this.with((BaseTextureFormatImpl<?>) format, InternalAttributeUsage.TEXTURE);
        return uncheckedCast(this);
    }

    @Override
    public BUILDER with(@NonNull L layout) {
        ((BaseLayoutImpl) layout).origFormatsUsages.forEach(this::with);
        return uncheckedCast(this);
    }
}
