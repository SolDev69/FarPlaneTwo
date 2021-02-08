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

package net.daporkchop.fp2.mode.common.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.client.AllocatedGLBuffer;
import net.daporkchop.fp2.client.gl.camera.IFrustum;
import net.daporkchop.fp2.client.gl.object.GLBuffer;
import net.daporkchop.fp2.client.gl.object.VertexArrayObject;
import net.daporkchop.fp2.mode.api.Compressed;
import net.daporkchop.fp2.mode.api.IFarPos;
import net.daporkchop.fp2.mode.api.piece.IFarPiece;
import net.daporkchop.fp2.util.Constants;
import net.daporkchop.fp2.util.SimpleRecycler;
import net.daporkchop.fp2.util.math.Volume;
import net.daporkchop.fp2.util.threading.ClientThreadExecutor;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.util.GenericMatcher;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

import static net.daporkchop.fp2.client.ClientConstants.*;
import static net.daporkchop.fp2.client.gl.OpenGL.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class AbstractFarRenderCache<POS extends IFarPos, P extends IFarPiece> {
    protected final AbstractFarRenderer<POS, P> renderer;

    protected final AbstractFarRenderTree<POS, P> tree;
    protected final Map<POS, Compressed<POS, P>> pieces = new ConcurrentHashMap<>();

    protected final IntFunction<POS[]> posArray;
    protected final IntFunction<P[]> pieceArray;

    protected final FarRenderIndex index;
    protected final VertexArrayObject vao = new VertexArrayObject();

    protected final AllocatedGLBuffer vertices;
    protected final int vertexSize;

    protected final AllocatedGLBuffer indices;
    protected final int indexType;
    protected final int indexSize;

    protected final int passes;

    protected final IFarRenderBaker<POS, P> baker;

    public AbstractFarRenderCache(@NonNull AbstractFarRenderer<POS, P> renderer, int vertexSize) {
        this.renderer = renderer;

        Class<POS> posClass = GenericMatcher.uncheckedFind(this.getClass(), AbstractFarRenderCache.class, "POS");
        this.posArray = size -> uncheckedCast(Array.newInstance(posClass, size));
        Class<P> pieceClass = GenericMatcher.uncheckedFind(this.getClass(), AbstractFarRenderCache.class, "P");
        this.pieceArray = size -> uncheckedCast(Array.newInstance(pieceClass, size));

        this.vertexSize = vertexSize;
        switch (this.indexType = (this.baker = renderer.baker()).indexType()) {
            case GL_UNSIGNED_BYTE:
                this.indexSize = 1;
                break;
            case GL_UNSIGNED_SHORT:
                this.indexSize = 2;
                break;
            case GL_UNSIGNED_INT:
                this.indexSize = 4;
                break;
            default:
                throw new IllegalArgumentException(PStrings.fastFormat("Invalid index type: %d", this.indexType));
        }

        this.passes = positive(this.baker.passes(), "passes");

        this.tree = this.createTree();

        this.vertices = AllocatedGLBuffer.create(GL_DYNAMIC_DRAW, this.vertexSize, true);
        this.indices = AllocatedGLBuffer.create(GL_DYNAMIC_DRAW, this.indexSize, true);

        this.index = new FarRenderIndex(this);

        this.rebuildVAO();
    }

    protected void rebuildVAO() {
        checkGLError("pre rebuild VAO");
        int attribs = this.baker.vertexAttributes();
        try (VertexArrayObject vao = this.vao.bind()) {
            for (int i = 0; i <= attribs; i++) {
                glEnableVertexAttribArray(i);
            }

            try (GLBuffer vbo = this.renderer.drawCommandBuffer.bind(GL_ARRAY_BUFFER)) {
                glVertexAttribIPointer(0, 4, GL_INT, FarRenderIndex.ENTRY_SIZE * INT_SIZE, 0L);
                glVertexAttribDivisor(0, 1);
                vao.putDependency(0, vbo);
            }

            try (AllocatedGLBuffer vbo = this.vertices.bind(GL_ARRAY_BUFFER)) {
                this.baker.assignVertexAttributes();
                for (int i = 1; i <= attribs; i++) {
                    vao.putDependency(i, vbo);
                }
            }

            vao.putElementArray(this.indices.bind(GL_ELEMENT_ARRAY_BUFFER));
        } finally {
            for (int i = 0; i <= attribs; i++) {
                glDisableVertexAttribArray(i);
            }

            this.indices.close();
        }
        checkGLError("post rebuild VAO");
    }

    protected abstract AbstractFarRenderTree<POS, P> createTree();

    public void receivePiece(@NonNull Compressed<POS, P> pieceIn) {
        final int maxLevel = this.renderer.maxLevel;

        this.pieces.put(pieceIn.pos(), pieceIn);

        this.baker.bakeOutputs(pieceIn.pos())
                .forEach(pos -> {
                    if (pos.level() < 0 || pos.level() > maxLevel) {
                        return;
                    }

                    Compressed<POS, P>[] compressedInputPieces = uncheckedCast(this.baker.bakeInputs(pos)
                            .map(this.pieces::get)
                            .toArray(Compressed[]::new));

                    Compressed<POS, P> piece = Arrays.stream(compressedInputPieces).filter(p -> p != null && pos.equals(p.pos())).findAny().orElse(null);
                    if (piece == null) {
                        return;
                    }

                    RENDER_WORKERS.submit(pos, () -> {
                        if (this.pieces.get(pos) != piece) { //piece was modified before this task could be executed
                            return;
                        }

                        //TODO: allocate these buffers from a shared pool to prevent OOMEs if the render workers are faster than the client thread
                        ByteBuf vertices = Constants.allocateByteBufNativeOrder(this.baker.estimatedVerticesBufferCapacity());
                        ByteBuf[] indices = new ByteBuf[this.passes];
                        int estimatedIndicesBufferCapacity = this.baker.estimatedIndicesBufferCapacity();
                        for (int i = 0; i < this.passes; i++) {
                            indices[i] = Constants.allocateByteBufNativeOrder(estimatedIndicesBufferCapacity);
                        }
                        try {
                            SimpleRecycler<P> recycler = this.renderer.mode().pieceRecycler();
                            P[] inputPieces = this.pieceArray.apply(compressedInputPieces.length);
                            try {
                                for (int i = 0; i < inputPieces.length; i++) { //inflate pieces
                                    if (compressedInputPieces[i] != null) {
                                        inputPieces[i] = compressedInputPieces[i].inflate(recycler);
                                    }
                                }

                                this.baker.bake(pos, inputPieces, vertices, indices);

                                //upload to GPU on client thread
                                vertices.retain();
                                for (int i = 0; i < indices.length; i++) {
                                    ByteBuf buf = indices[i];
                                    if (!buf.isReadable()) {
                                        buf.release();
                                        indices[i] = Unpooled.EMPTY_BUFFER;
                                    } else {
                                        buf.retain();
                                    }
                                }
                                ClientThreadExecutor.INSTANCE.execute(() -> this.addPiece(piece, vertices, indices));
                            } finally { //release pieces again
                                for (int i = 0; i < inputPieces.length; i++) {
                                    if (inputPieces[i] != null) {
                                        recycler.release(inputPieces[i]);
                                    }
                                }
                            }
                        } finally {
                            vertices.release();
                            for (ByteBuf buf : indices) {
                                buf.release();
                            }
                        }
                    });
                });
    }

    public void addPiece(@NonNull Compressed<POS, P> piece, @NonNull ByteBuf vertices, @NonNull ByteBuf[] indices) {
        try {
            POS pos = piece.pos();
            if (this.pieces.get(pos) != piece) {
                return;
            }

            try (AllocatedGLBuffer verticesBuffer = this.vertices.bind(GL_ARRAY_BUFFER);
                 AllocatedGLBuffer indicesBuffer = this.indices.bind(GL_ELEMENT_ARRAY_BUFFER)) {
                this.tree.putRenderData(pos, vertices, indices);
            }
        } finally {
            vertices.release();
            for (ByteBuf buf : indices) {
                buf.release();
            }
        }
    }

    public void unloadPiece(@NonNull POS pos) {
        RENDER_WORKERS.submit(pos, () -> { //make sure that any in-progress bake tasks are finished before the piece is removed
            ClientThreadExecutor.INSTANCE.execute(() -> this.removePiece(pos));
        });
    }

    public void removePiece(@NonNull POS pos) {
        if (this.pieces.remove(pos) == null) {
            Constants.LOGGER.warn("Attempted to unload already non-existent piece at {}!", pos);
        }
        this.tree.removeNode(pos);
    }

    public FarRenderIndex rebuildIndex(@NonNull Volume[] ranges, @NonNull IFrustum frustum) {
        this.index.reset();
        this.tree.select(ranges, frustum, this.index);
        return this.index;
    }
}
