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

package net.daporkchop.fp2.client;

import lombok.NonNull;
import net.daporkchop.fp2.client.common.TerrainRenderer;
import net.daporkchop.fp2.client.height.HeightTerrainRenderer;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.config.Config;

/**
 * @author DaPorkchop_
 */
public enum RenderStrategy {
    @Config.Comment("Renders a simple 2D heightmap of the world. Overhangs are not supported.")
    HEIGHT_2D {
        @Override
        public TerrainRenderer createTerrainRenderer(@NonNull World world) {
            return new HeightTerrainRenderer();
        }
    },
    FULL_3D {
        @Override
        public TerrainRenderer createTerrainRenderer(@NonNull World world) {
            throw new UnsupportedOperationException(); //TODO
        }
    };

    public abstract TerrainRenderer createTerrainRenderer(@NonNull World world);
}
