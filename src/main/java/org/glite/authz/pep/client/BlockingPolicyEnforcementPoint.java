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

package org.glite.authz.pep.client;

import net.jcip.annotations.ThreadSafe;

import org.glite.authz.common.AuthorizationServiceException;
import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Response;
import org.glite.authz.pep.client.config.PEPClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A client for contacting a remote PEP daemon. */
@ThreadSafe
public class BlockingPolicyEnforcementPoint {
    
    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(BlockingPolicyEnforcementPoint.class);

    /** PEP client configuration. */
    private PEPClientConfiguration clientConfig;

    /** Client used to communicate with the daemon. */
    private PEPDaemonClient daemonClient;

    /**
     * Constructor.
     * 
     * @param configuration the configuration for the client
     */
    public BlockingPolicyEnforcementPoint(PEPClientConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration may no be null");
        }
        clientConfig = configuration;

        daemonClient = new PEPDaemonClient(configuration);
    }

    /**
     * Makes an authorization request. This method takes the request, sends it to a PEP daemon, gets the results, and
     * operates on any obligations within the result.
     * 
     * @param request the request to be made
     * 
     * @return the response to the request
     * 
     * @throws AuthorizationServiceException thrown if there is a problem contacting the PEP daemon or processing
     *             returned obligations
     */
    public Response authorize(Request request) throws AuthorizationServiceException {
        log.debug("Processing authorization request: {}", request);
        
        daemonClient.runPolicyInformationPoints(request, clientConfig.getPolicyInformationPoints());
        
        Response response;
        for (String pep : clientConfig.getPepDaemonEndpoints()) {
            response = daemonClient.performRequest(pep, request);
            if (response != null) {
                return response;
            }
        }

        throw new AuthorizationServiceException("No policy enformcement daemon could be contacted.");
    }
}