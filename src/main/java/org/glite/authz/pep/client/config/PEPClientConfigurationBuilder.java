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

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.glite.authz.common.config.AbstractConfigurationBuilder;

/** Builder of thread-safe {@link PEPClientConfiguration}s. */
@NotThreadSafe
public class PEPClientConfigurationBuilder extends AbstractConfigurationBuilder<PEPClientConfiguration> {

    /** Registered PEP daemon endpoints. */
    private List<String> pepDaemons;

    /** Constructor. */
    public PEPClientConfigurationBuilder() {
        super();
        pepDaemons = new ArrayList<String>();
    }
    
    /**
     * Constructor thats creates a builder factory with the same settings as the given prototype configuration.
     * 
     * @param prototype the prototype configuration whose values will be used to initialize this builder
     */
    public PEPClientConfigurationBuilder(PEPClientConfiguration prototype){
        pepDaemons = prototype.getPepDaemonEndpoints();
        if(pepDaemons == null){
            pepDaemons = new ArrayList<String>();
        }
    }

    /** {@inheritDoc} */
    public PEPClientConfiguration build() {
        PEPClientConfiguration config = new PEPClientConfiguration();
        populateConfiguration(config);
        config.getPepDaemonEndpoints().addAll(pepDaemons);
        return config;
    }

    /**
     * Gets the registered PEP daemon endpoint URLs.
     * 
     * @return registered PEP daemon endpoint URLs
     */
    public List<String> getPepDaemonEndpoints() {
        return pepDaemons;
    }
}