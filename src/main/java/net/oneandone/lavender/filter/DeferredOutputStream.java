/**
 * Copyright 1&1 Internet AG, https://github.com/1and1/
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
package net.oneandone.lavender.filter;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** OutputStream that defers creation of the target output stream until the target is actually used. */
public abstract class DeferredOutputStream extends ServletOutputStream {
    private OutputStream target;

    public DeferredOutputStream() {
        target = null;
    }

    protected abstract OutputStream createTarget() throws IOException;

    private OutputStream target() throws IOException {
        if (target == null) {
            target = createTarget();
        }
        return target;
    }

    @Override
    public void write(int b) throws IOException {
        target().write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        target().write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        target().flush();
    }

    @Override
    public void close() throws IOException {
        target().close();
    }
}
