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
import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings {
    public final String svnUsername;
    public final String svnPassword;
    public final List<Node> sshKeys;

    public Settings(String svnUsername, String svnPassword, List<Node> sshKeys) {
        this.svnUsername = svnUsername;
        this.svnPassword = svnPassword;
        this.sshKeys = sshKeys;
    }

    public static Settings load(World world) throws IOException {
        Properties properties;
        List<Node> sshKeys;

        properties = world.getHome().join(".lavender.settings").readProperties();
        sshKeys = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("ssh.")) {
                sshKeys.add(world.file(properties.getProperty(key)));
            }
        }
        return new Settings(properties.getProperty("svn.username"), properties.getProperty("svn.password"), sshKeys);
    }
}
