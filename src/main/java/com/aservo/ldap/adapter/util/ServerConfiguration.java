/*
 * Copyright (c) 2019 ASERVO Software GmbH
 * contact@aservo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aservo.ldap.adapter.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;


public class ServerConfiguration {

    public static final String CONFIG_CACHE_DIR = "cache-directory";
    public static final String CONFIG_BIND_ADDRESS = "bind.address";
    public static final String CONFIG_SSL_ENABLED = "ssl.enabled";
    public static final String CONFIG_SSL_KEY_STORE = "ssl.keystore";
    public static final String CONFIG_SSL_CERTIFICATE_PW = "ssl.certificate.password";
    public static final String CONFIG_SUPPORT_MEMBER_OF = "support.member-of";

    private final Properties serverProperties;
    private final Properties crowdProperties;
    private final File cacheDir;
    private final String host;
    private final int port;
    private final boolean sslEnabled;
    private final String keyStore;
    private final String certificatePassword;
    private final MemberOfSupport memberOfSupport;

    public ServerConfiguration(Properties serverProperties, Properties crowdProperties) {

        this.serverProperties = serverProperties;
        this.crowdProperties = crowdProperties;

        cacheDir = new File(serverProperties.getProperty(CONFIG_CACHE_DIR));

        if (cacheDir.exists()) {

            try {

                FileUtils.deleteDirectory(cacheDir);

            } catch (IOException e) {

                throw new UncheckedIOException(e);
            }
        }

        if (!cacheDir.mkdirs())
            throw new UncheckedIOException(new IOException("Cannot create cache directory."));

        String bindAddressValue = serverProperties.getProperty(CONFIG_BIND_ADDRESS);

        if (bindAddressValue == null)
            bindAddressValue = "localhost:10389";

        String[] bindAddressParts = bindAddressValue.split(":");

        if (bindAddressParts.length != 2 || bindAddressParts[0].isEmpty() || bindAddressParts[1].isEmpty())
            throw new IllegalArgumentException("Cannot parse value for " + CONFIG_BIND_ADDRESS);

        host = bindAddressParts[0];
        port = Integer.parseInt(bindAddressParts[1]);

        // SSL support
        sslEnabled = Boolean.parseBoolean(serverProperties.getProperty(CONFIG_SSL_ENABLED, "false"));

        if (sslEnabled) {

            keyStore = serverProperties.getProperty(CONFIG_SSL_KEY_STORE);

            if (keyStore == null)
                throw new IllegalArgumentException("Missing value for " + CONFIG_SSL_KEY_STORE);

            certificatePassword = serverProperties.getProperty(CONFIG_SSL_CERTIFICATE_PW);

            if (certificatePassword == null)
                throw new IllegalArgumentException("Missing value for " + CONFIG_SSL_CERTIFICATE_PW);

        } else {

            keyStore = null;
            certificatePassword = null;
        }

        String memberOfSupportValue = serverProperties.getProperty(CONFIG_SUPPORT_MEMBER_OF, "normal");
        memberOfSupport = MemberOfSupport.valueOf(memberOfSupportValue.toUpperCase().replace('-', '_'));
    }

    public Properties getServerProperties() {

        return serverProperties;
    }

    public Properties getCrowdProperties() {

        return crowdProperties;
    }

    public File getCacheDir() {

        return cacheDir;
    }

    public String getHost() {

        return host;
    }

    public int getPort() {

        return port;
    }

    public boolean isSslEnabled() {

        return sslEnabled;
    }

    public String getKeyStore() {

        return keyStore;
    }

    public String getCertificatePassword() {

        return certificatePassword;
    }

    public MemberOfSupport getMemberOfSupport() {

        return memberOfSupport;
    }
}
