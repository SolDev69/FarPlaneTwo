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

package net.daporkchop.fp2.util;

import lombok.NonNull;
import net.daporkchop.fp2.config.FP2Config;
import net.daporkchop.fp2.mode.api.IFarPos;
import net.daporkchop.fp2.mode.api.IFarTile;
import net.daporkchop.fp2.mode.api.ctx.IFarServerContext;
import net.daporkchop.fp2.mode.api.ctx.IFarWorldServer;
import net.daporkchop.fp2.util.annotation.CalledFromServerThread;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @author DaPorkchop_
 */
public interface IFarPlayer {
    IFarWorldServer fp2_IFarPlayer_world();

    FP2Config fp2_IFarPlayer_config();

    @CalledFromServerThread
    void fp2_IFarPlayer_serverConfig(FP2Config serverConfig);

    @CalledFromServerThread
    void fp2_IFarPlayer_clientConfig(FP2Config clientConfig);

    @CalledFromServerThread
    <POS extends IFarPos, T extends IFarTile> IFarServerContext<POS, T> fp2_IFarPlayer_activeContext();

    void fp2_IFarPlayer_sendPacket(@NonNull IMessage packet);

    @CalledFromServerThread
    void fp2_IFarPlayer_update();

    @CalledFromServerThread
    void fp2_IFarPlayer_close();
}
