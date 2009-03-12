/*
 * Copyright 2008 EGEE Collaboration
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

package org.glite.authz.pep.client.config;

import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import net.jcip.annotations.ThreadSafe;

import org.glite.authz.common.config.AbstractIniConfigurationParser;
import org.glite.authz.common.config.ConfigurationException;
import org.glite.authz.common.config.IniConfigUtil;
import org.ini4j.Ini;
import org.ini4j.Ini.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parser for a {@link org.glite.authz.pep.client.BlockingPolicyEnforcementPoint} configuration file. */
@ThreadSafe
public class PEPClientIniConfigurationParser extends AbstractIniConfigurationParser<PEPClientConfiguration> {

    /** The name of the {@value} INI header which contains client configuration properties. */
    public static final String CLIENT_SECTION_HEADER = "CLIENT";

    /** The name of the {@value} property which gives the space-delimited PEP daemon endpoint URLs. */
    public static final String PEPD_PROP = "pepds";

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(PEPClientIniConfigurationParser.class);

    /** Constructor. */
    public PEPClientIniConfigurationParser() {
    }

    /** {@inheritDoc} */
    public PEPClientConfiguration parse(String iniString) throws ConfigurationException {
        return parseIni(new StringReader(iniString));
    }

    /** {@inheritDoc} */
    public PEPClientConfiguration parse(Reader iniReader) throws ConfigurationException {
        return parseIni(iniReader);
    }

    /**
     * Parses the incoming INI.
     * 
     * @param iniReader reader for the INI
     * 
     * @return constructed configuration
     * 
     * @throws ConfigurationException thrown if there is a problem parsing the client configuration
     */
    private PEPClientConfiguration parseIni(Reader iniReader) throws ConfigurationException {
        PEPClientConfigurationBuilder configBuilder = new PEPClientConfigurationBuilder();

        Ini clientIni = new Ini();
        try {
            clientIni.load(iniReader);
        } catch (Exception e) {
            throw new ConfigurationException("Unable to read INI configration", e);
        }

        processClientConfiguration(clientIni, configBuilder);
        return configBuilder.build();
    }

    /**
     * Processes the information within the DAEMON configuration section.
     * 
     * @param iniFile the INI configuration file
     * @param configBuilder the daemon configuration builder
     * 
     * @throws ConfigurationException thrown if there is a problem processing the daemon configuration
     */
    private void processClientConfiguration(Ini iniFile, PEPClientConfigurationBuilder configBuilder)
            throws ConfigurationException {
        Section configSection = iniFile.get(CLIENT_SECTION_HEADER);

        int maxConnections = getMaximumRequests(configSection);
        log.debug("max requests: {}", maxConnections);
        configBuilder.setMaxConnections(maxConnections);

        int connTimeout = getConnectionTimeout(configSection);
        log.debug("connection timeout: {}", connTimeout);
        configBuilder.setConnectionTimeout(connTimeout);

        int recBufferSize = getReceiveBufferSize(configSection);
        log.debug("receive buffer size: {}", recBufferSize);
        configBuilder.setReceiveBufferSize(recBufferSize);

        int sendBufferSize = getSendBufferSize(configSection);
        log.debug("send buffer size: {}", sendBufferSize);
        configBuilder.setSendBufferSize(sendBufferSize);

        String pepdStr = IniConfigUtil.getString(configSection, PEPD_PROP);
        log.debug("policy enforcement point endpoints: {}", pepdStr);
        StringTokenizer pepds = new StringTokenizer(pepdStr, " ");
        while (pepds.hasMoreTokens()) {
            configBuilder.getPepDaemonEndpoints().add(pepds.nextToken());
        }

        processPolicyInformationPoints(iniFile, configSection, configBuilder);

        processObligationHandlers(iniFile, configSection, configBuilder);
    }

}