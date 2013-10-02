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

import net.oneandone.lavender.config.Docroot;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.filter.Filter;
import net.oneandone.sushi.util.Separator;
import net.oneandone.sushi.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class LavenderProperties {
    private static final Logger LOG = LoggerFactory.getLogger(LavenderProperties.class);

    public static final String MODULE_PROPERTIES = "PUSTEFIX-INF/lavender.properties";
    private static final String APP_PROPERTIES = "WEB-INF/lavender.properties";

    public static Properties getAppProperties(Node webapp) throws IOException {
        Node src;

        src = webapp.join(LavenderProperties.APP_PROPERTIES);
        if (!src.exists()) {
            // TODO: dump this compatibility check as soon as I have ITs with new wars
            src = webapp.join("WEB-INF/lavendel.properties");
            if (!src.exists()) {
                throw new IOException("lavender.properties not found");
            }
        }
        return src.readProperties();
    }

    public static LavenderProperties parse(Properties properties) {
        return parse(properties, DefaultModule.DEFAULT_INCLUDES);
    }

    public static LavenderProperties parse(Properties properties, List<String> defaultIncludes) {
        String value;
        String name;
        LavenderProperties result;
        String svnurl;
        Filter filter;
        String targetPathPrefix;
        String resourcePathPrefix;
        String type;
        boolean lavendelize;
        String livePath;

        result = new LavenderProperties(eat(properties, "livePath", null));
        for (String prefix : svnPrefixes(properties)) {
            value = (String) properties.remove(prefix);
            name = prefix.substring(SvnProperties.SVN_PREFIX.length());
            svnurl = Strings.removeLeftOpt(value, "scm:svn:");
            filter = eatFilter(properties, prefix, defaultIncludes);
            targetPathPrefix = eatTargetPathPrefix(properties, prefix);
            resourcePathPrefix = eat(properties, prefix + ".sourcePathPrefix", "");
            type = eatType(properties, prefix);
            lavendelize = eatBoolean(properties, prefix + ".lavendelize", true);
            livePath = eat(properties, prefix + ".livePath", null);
            result.configs.add(new SvnProperties(name, filter, svnurl, type, lavendelize, resourcePathPrefix, targetPathPrefix, livePath));
        }
        if (properties.size() > 0) {
            throw new IllegalArgumentException("unknown properties: " + properties);
        }
        return result;
    }

    private static String eatType(Properties properties, String prefix) {
        String type;
        String value;

        type = eat(properties, prefix + ".type", Docroot.WEB);
        if (type == null) {
            value = eat(properties, prefix + ".storage", null);
            if (value.startsWith("flash-")) {
                // TODO: dump
                LOG.warn("CAUTION: out-dated storage configured - use type instead");
                type = Docroot.FLASH;
            } else {
                throw new IllegalArgumentException("storage no longer supported: " + value);
            }
        }
        return type;
    }

    private static String eatTargetPathPrefix(Properties properties, String prefix) {
        String targetPathPrefix;

        targetPathPrefix = eat(properties, prefix + ".targetPathPrefix", "");
        if (targetPathPrefix == null) {
            LOG.warn("CAUTION: out-dated pathPrefix - use targetPathPrefix instead");
            targetPathPrefix = eat(properties, prefix + ".pathPrefix", null);
        }
        return targetPathPrefix;
    }

    private static Filter eatFilter(Properties properties, String prefix, List<String> defaultIncludes) {
        Filter result;

        result = new Filter();
        result.include(eatList(properties, prefix + ".includes", defaultIncludes));
        result.exclude(eatList(properties, prefix + ".excludes", Collections.EMPTY_LIST));
        return result;
    }

    private static List<String> eatList(Properties p, String key, List<String> dflt) {
        String result;

        result = eat(p, key, null);
        return result == null ? dflt : Separator.SPACE.split(result);
    }

    private static boolean eatBoolean(Properties p, String key, boolean dflt) {
        String result;

        result = eat(p, key, null);
        if (result == null) {
            return dflt;
        }
        switch (result) {
            case "true": return true;
            case "false": return false;
            default: throw new IllegalArgumentException("true or false expected, got " + result);
        }
    }

    private static String eat(Properties p, String key, String dflt) {
        String result;

        result = (String) p.remove(key);
        return result == null ? dflt : result;
    }

    private static List<String> svnPrefixes(Properties properties) {
        List<String> result;

        result = new ArrayList<>();
        for (String name : properties.stringPropertyNames()) {
            if (name.startsWith(SvnProperties.SVN_PREFIX)) {
                if (Strings.count(name, ".") == 1) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    //--

    public final String livePath;
    public final Collection<SvnProperties> configs;

    public LavenderProperties(String livePath) {
        this.livePath = livePath;
        this.configs = new ArrayList<>();
    }

    public void addModules(boolean prod, World world, String svnUsername, String svnPassword, List<Module> result) throws IOException {
        for (SvnProperties config : configs) {
            result.add(config.create(prod, world, svnUsername, svnPassword));
        }
    }
}
