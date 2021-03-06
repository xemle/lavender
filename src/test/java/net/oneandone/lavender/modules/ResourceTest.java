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
package net.oneandone.lavender.modules;

import net.oneandone.lavender.index.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceTest {
    private Resource resource;

    @Before
    public void setup() throws IOException {
        resource = DefaultResource.forBytes(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x7F, (byte) 0x80, (byte) 0x81, (byte) 0xFF},
                "modules/x/img/close.gif");
    }

    @Test
    public void testData() throws IOException {
        byte[] data = resource.getData();
        assertNotNull(data);
        assertEquals(6, data.length);
        assertEquals(0x00, data[0]);
        assertEquals(0x01, data[1]);
        assertEquals(0x7F, data[2]);
        assertEquals((byte) 0x80, data[3]);
        assertEquals((byte) 0x81, data[4]);
        assertEquals((byte) 0xFF, data[5]);
    }

    @Test
    public void testGetOriginalPath() {
        assertEquals("modules/x/img/close.gif", resource.getPath());
    }

    @Test
    public void testGetLavendelizedPath() throws IOException {
        assertEquals("852/e7d76cdb8af7395cd039c0ecc293a/folder/close.gif",
                resource.labelLavendelized("", "folder").getLavendelizedPath());
    }

    @Test
    public void testMd5() throws IOException {
        byte[] md5 = resource.md5(resource.getData());
        assertNotNull(md5);
        assertEquals(16, md5.length);
        Assert.assertEquals("852e7d76cdb8af7395cd039c0ecc293a", Hex.encodeString(md5));
    }

    @Test
    public void testToString() {
        String string = resource.toString();
        assertNotNull(string);
    }

}
