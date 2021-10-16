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

package net.daporkchop.fp2.net.packet.debug.server;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.daporkchop.fp2.debug.util.DebugStats;
import net.daporkchop.fp2.util.annotation.DebugOnly;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import static net.daporkchop.fp2.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@DebugOnly
public class SPacketDebugUpdateStatistics implements IMessage {
    protected DebugStats.Tracking tracking;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tracking = GSON.fromJson(readString(buf), DebugStats.Tracking.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeString(buf, GSON.toJson(this.tracking));
    }
}
