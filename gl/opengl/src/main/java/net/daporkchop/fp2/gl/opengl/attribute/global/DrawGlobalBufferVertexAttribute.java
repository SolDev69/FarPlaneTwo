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

package net.daporkchop.fp2.gl.opengl.attribute.global;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.gl.attribute.global.DrawGlobalBuffer;
import net.daporkchop.fp2.gl.attribute.global.DrawGlobalFormat;
import net.daporkchop.fp2.gl.attribute.global.DrawGlobalWriter;
import net.daporkchop.fp2.gl.buffer.BufferUsage;
import net.daporkchop.fp2.gl.opengl.attribute.BaseAttributeBufferImpl;
import net.daporkchop.fp2.gl.opengl.attribute.common.VertexAttributeBuffer;
import net.daporkchop.fp2.gl.opengl.attribute.struct.format.InterleavedStructFormat;
import net.daporkchop.fp2.gl.opengl.buffer.BufferTarget;
import net.daporkchop.fp2.gl.opengl.buffer.GLBufferImpl;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class DrawGlobalBufferVertexAttribute<S> extends BaseAttributeBufferImpl<S, DrawGlobalFormatVertexAttribute<S>, DrawGlobalFormat<S>> implements DrawGlobalBuffer<S>, VertexAttributeBuffer {
    protected final InterleavedStructFormat<S> structFormat;

    protected final GLBufferImpl buffer;
    protected final long stride;

    protected int capacity;

    public DrawGlobalBufferVertexAttribute(@NonNull DrawGlobalFormatVertexAttribute<S> format, @NonNull BufferUsage usage) {
        super(format);
        this.structFormat = format.structFormat();

        this.buffer = this.gl.createBuffer(usage);
        this.stride = format.structFormat().stride();
    }

    @Override
    public void close() {
        this.buffer.close();
    }

    @Override
    public void resize(int capacity) {
        this.capacity = notNegative(capacity, "capacity");
        this.buffer.resize(capacity * this.stride);
    }

    @Override
    public void set(int index, @NonNull DrawGlobalWriter<S> _writer) {
        DrawGlobalWriterVertexAttribute<S> writer = (DrawGlobalWriterVertexAttribute<S>) _writer;
        checkArg(writer.structFormat() == this.structFormat, "mismatched struct formats!");
        checkIndex(this.capacity, index);

        this.buffer.uploadRange(index * this.stride, writer.addr, this.stride);
    }

    @Override
    public void configureVAO(@NonNull int[] attributeIndices) {
        this.buffer.bind(BufferTarget.ARRAY_BUFFER, target -> this.structFormat.configureVAO(this.gl.api(), attributeIndices));

        for (int attributeIndex : attributeIndices) { //configure divisors
            this.gl.api().glVertexAttribDivisor(attributeIndex, 1);
        }
    }
}