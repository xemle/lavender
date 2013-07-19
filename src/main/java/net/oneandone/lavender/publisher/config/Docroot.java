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
package net.oneandone.lavender.publisher.config;

import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;

import java.util.Arrays;
import java.util.List;

public class Docroot {
    public final String docroot;

    public final List<Alias> aliases;

    public Docroot(String docroot, Alias ... aliases) {
        this(docroot, Arrays.asList(aliases));
    }

    public Docroot(String docroot, List<Alias> aliases) {
        if (docroot.startsWith("/") || docroot.endsWith("/")) {
            throw new IllegalArgumentException(docroot);
        }
        this.docroot = docroot;
        this.aliases = aliases;
    }

    public Node node(Node host) throws NodeInstantiationException {
        return host.join(docroot);
    }

}