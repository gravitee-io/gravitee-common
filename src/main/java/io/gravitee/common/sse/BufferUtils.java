/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.sse;

import io.gravitee.gateway.api.buffer.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BufferUtils {

    private static Buffer[] split(Buffer buffer, int index) {
        var first = Buffer.buffer(Arrays.copyOf(buffer.getBytes(), index));
        var second = Buffer.buffer(Arrays.copyOfRange(buffer.getBytes(), index, buffer.length()));
        return new Buffer[] { first, second };
    }

    public static List<Buffer> split(Buffer buffer, byte[] seq) {
        List<Buffer> result = new ArrayList<>();
        int index;
        while ((index = BufferUtils.searchSeq(buffer, seq)) >= 0) {
            var buffers = BufferUtils.split(buffer, index + seq.length);
            buffer = buffers[1];
            result.add(buffers[0]);
        }
        if (buffer.length() > 0) {
            result.add(buffer);
        }
        return result;
    }

    public static int searchSeq(Buffer buffer, byte[] seq) {
        return searchSeq(buffer, seq, 0).orElse(-1);
    }

    public static OptionalInt searchSeq(Buffer buffer, byte[] seq, int startIdx) {
        byte[] bytes = buffer.getBytes();
        for (int i = startIdx; i < buffer.length(); i++) {
            boolean found = true;
            for (int j = 0; found && j < seq.length; j++) {
                found = i + j < buffer.length() && bytes[i + j] == seq[j];
            }
            if (found) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }
}
