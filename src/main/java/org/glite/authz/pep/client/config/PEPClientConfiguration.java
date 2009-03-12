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

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.glite.authz.common.config.AbstractConfiguration;

/** Implementation of {@link PEPClientConfiguration}. */
@ThreadSafe
public class PEPClientConfiguration extends AbstractConfiguration {

    /** Registered PEP daemon endpoints. */
    private List<String> pepdEndpoints;

    /** Constructor. */
    protected PEPClientConfiguration() {
        pepdEndpoints = null;
    }

    /**
     * Gets an immutable list of PEP daemon endpoints.
     * 
     * @return list of PEP daemon endpoints
     */
    public List<String> getPepDaemonEndpoints() {
        return pepdEndpoints;
    }

    /**
     * Sets the PEP daemon endopints.
     * 
     * @param endpoints PEP daemon endopints
     */
    protected synchronized final void setPepDaemonEndpoints(List<String> endpoints) {
        if (endpoints == null || endpoints.size() == 0) {
            return;
        }

        if (pepdEndpoints != null) {
            throw new IllegalStateException("PEP daemon endpoints have already been set, they may not be changed");
        }

        pepdEndpoints = Collections.unmodifiableList(endpoints);
    }
}