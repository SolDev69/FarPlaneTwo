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

#include "NativeFastLayer.h"

inline int32_t eval(fp2::biome::fastlayer::rng& rng, int32_t val) {
    return val > 0
            ? rng.nextInt<299999>() + 2
            : 0;
}

using layer = fp2::biome::fastlayer::translation_layer<eval>;

FP2_JNI(void, NativeFastLayerRiverInit, getGrid0) (JNIEnv* env, jobject obj,
        jlong seed, jint x, jint z, jint sizeX, jint sizeZ, jintArray _inout) {
    layer{}.grid(env, seed, x, z, sizeX, sizeZ, _inout);
}

FP2_JNI(void, NativeFastLayerRiverInit, multiGetGrids0) (JNIEnv* env, jobject obj,
        jlong seed, jint x, jint z, jint size, jint dist, jint count, jintArray _inout) {
    layer{}.grid_multi(env, seed, x, z, size, dist, count, _inout);
}