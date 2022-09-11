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
 */

package net.daporkchop.fp2.gl.opengl.attribute.struct.property.basic;

import lombok.Data;
import lombok.NonNull;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.ComponentInterpretation;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.ComponentType;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.StructProperty;
import net.daporkchop.fp2.gl.opengl.attribute.struct.type.GLSLBasicType;

/**
 * @author DaPorkchop_
 */
@Data
public class BasicScalarProperty implements StructProperty.Components {
    @NonNull
    protected final ComponentType logicalStorageType;
    @NonNull
    protected final ComponentInterpretation componentInterpretation;

    @Override
    public int cols() {
        return 1;
    }

    @Override
    public int rows() {
        return 1;
    }

    @Override
    public int components() {
        return 1;
    }

    @Override
    public GLSLBasicType glslType() {
        return this.componentInterpretation.outputType();
    }
}
