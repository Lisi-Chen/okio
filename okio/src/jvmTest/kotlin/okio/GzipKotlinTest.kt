/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package okio

import kotlin.test.assertEquals
import okio.ByteString.Companion.decodeHex
import org.junit.Test

class GzipKotlinTest {
  @Test fun sink() {
    val data = Buffer()
    (data as Sink).gzip().buffer().use { gzip ->
      gzip.writeUtf8("Hi!")
    }
    assertEquals("1f8b0800000000000000f3c8540400dac59e7903000000", data.readByteString().hex())
  }

  @Test fun source() {
    val buffer = Buffer().write("1f8b0800000000000000f3c8540400dac59e7903000000".decodeHex())
    (buffer as Source).gzip().buffer().use { gzip ->
      assertEquals("Hi!", gzip.readUtf8())
    }
  }

  @Test fun extraLongXlen() {
    val xlen = 0xffff
    val buffer = Buffer()
      .write("1f8b0804000000000000".decodeHex())
      .writeShort(xlen)
      .write(ByteArray(xlen))
      .write("f3c8540400dac59e7903000000".decodeHex())
    (buffer as Source).gzip().buffer().use { gzip ->
      assertEquals("Hi!", gzip.readUtf8())
    }
  }
}
