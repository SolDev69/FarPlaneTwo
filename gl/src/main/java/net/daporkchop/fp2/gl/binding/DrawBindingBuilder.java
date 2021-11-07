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

package net.daporkchop.fp2.gl.binding;

import lombok.NonNull;
import net.daporkchop.fp2.gl.attribute.global.GlobalAttributeBuffer;
import net.daporkchop.fp2.gl.attribute.local.LocalAttributeBuffer;
import net.daporkchop.fp2.gl.attribute.uniform.UniformAttributeBuffer;
import net.daporkchop.fp2.gl.index.IndexBuffer;

/**
 * Builder for {@link DrawBinding}s.
 *
 * @param <B> the type of {@link DrawBinding} to construct
 * @author DaPorkchop_
 */
public interface DrawBindingBuilder<B extends DrawBinding> {
    /**
     * Adds a {@link LocalAttributeBuffer} which contain uniform attributes.
     *
     * @param uniforms the uniform attributes
     */
    DrawBindingBuilder<B> withUniforms(@NonNull UniformAttributeBuffer uniforms);

    /**
     * Adds a {@link GlobalAttributeBuffer} which contain global attributes.
     *
     * @param globals the global attributes
     */
    DrawBindingBuilder<B> withGlobals(@NonNull GlobalAttributeBuffer globals);

    /**
     * Adds a {@link LocalAttributeBuffer} which contain local attributes.
     *
     * @param locals the local attributes
     */
    DrawBindingBuilder<B> withLocals(@NonNull LocalAttributeBuffer locals);

    /**
     * @return the constructed {@link B}
     */
    B build();

    /**
     * @author DaPorkchop_
     */
    interface OptionallyIndexedStage extends DrawBindingBuilder<DrawBinding> {
        /**
         * Defines the {@link IndexBuffer} which contains the index data.
         *
         * @param indices the index data
         */
        DrawBindingBuilder<DrawBindingIndexed> withIndexes(@NonNull IndexBuffer indices);
    }
}