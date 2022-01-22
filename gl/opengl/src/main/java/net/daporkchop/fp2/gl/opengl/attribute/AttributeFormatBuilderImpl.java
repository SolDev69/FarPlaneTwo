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

package net.daporkchop.fp2.gl.opengl.attribute;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.fp2.gl.attribute.AttributeFormat;
import net.daporkchop.fp2.gl.attribute.AttributeFormatBuilder;
import net.daporkchop.fp2.gl.attribute.AttributeUsage;
import net.daporkchop.fp2.gl.opengl.OpenGL;
import net.daporkchop.fp2.gl.opengl.attribute.common.AttributeFormatImpl;
import net.daporkchop.fp2.gl.opengl.attribute.struct.StructInfo;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class AttributeFormatBuilderImpl<S> implements AttributeFormatBuilder<S> {
    @NonNull
    protected final OpenGL gl;
    @NonNull
    protected final Class<S> clazz;

    protected final Set<InternalAttributeUsage> usages = EnumSet.noneOf(InternalAttributeUsage.class);
    protected final Map<String, String> nameOverrides = new HashMap<>();

    @Override
    public AttributeFormatBuilder<S> rename(@NonNull String originalName, @NonNull String newName) {
        checkState(this.nameOverrides.putIfAbsent(originalName, newName) == null, "name %s cannot be overridden twice!", originalName);
        return this;
    }

    @Override
    public AttributeFormatBuilder<S> useFor(@NonNull AttributeUsage usage) {
        this.usages.add(InternalAttributeUsage.fromExternal(usage));
        return this;
    }

    @Override
    public AttributeFormatBuilder<S> useFor(@NonNull AttributeUsage... usages) {
        for (AttributeUsage usage : usages) {
            this.usages.add(InternalAttributeUsage.fromExternal(usage));
        }
        return this;
    }

    @Override
    public AttributeFormat<S> build() {
        for (AttributeFormatType type : AttributeFormatType.values()) {
            Optional<AttributeFormatImpl<S, ?>> optionalFormat = type.createFormat(this);
            if (optionalFormat.isPresent()) {
                return optionalFormat.get();
            }
        }

        throw new IllegalStateException(this.toString());
    }

    public StructInfo<S> structInfo() {
        return new StructInfo<>(this.clazz, this.nameOverrides);
    }
}
