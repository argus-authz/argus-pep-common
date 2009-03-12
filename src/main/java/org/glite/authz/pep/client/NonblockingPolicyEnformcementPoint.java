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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.jcip.annotations.ThreadSafe;

import org.glite.authz.common.AuthorizationServiceException;
import org.glite.authz.pep.client.config.PEPClientConfiguration;
import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A policy enforcement point daemon that does not block when making requests. */
@ThreadSafe
public class NonblockingPolicyEnformcementPoint {
    
    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(NonblockingPolicyEnformcementPoint.class);

    /** PEP client configuration. */
    private PEPClientConfiguration clientConfig;

    /** Client used to communicate with the daemon. */
    private PEPDaemonClient daemonClient;

    /** Pool of client threads. */
    private ExecutorService executorService;

    /**
     * Constructor.
     * 
     * @param config client configuration
     * @param numOfProcessingThreads number of threads that will service authorization requests
     */
    public NonblockingPolicyEnformcementPoint(PEPClientConfiguration config, int numOfProcessingThreads) {
        clientConfig = config;
        daemonClient = new PEPDaemonClient(config);
        executorService = Executors.newFixedThreadPool(numOfProcessingThreads);

    }

    /**
     * Makes an non-blocking authorization request. This method takes the request, sends it to a PEP daemon, gets the
     * results, and operates on any obligations within the result.
     * 
     * @param request the request to be made
     * 
     * @return the response to the request
     * 
     * @throws AuthorizationServiceException thrown if there is a problem contacting the PEP daemon or processing
     *             returned obligations
     */
    public Future<Response> authorize(Request request) throws AuthorizationServiceException {
        AuthorizationRequestJob authzRequestJob = new AuthorizationRequestJob(request);
        Future<Response> futureResult = executorService.submit(authzRequestJob);
        return futureResult;
    }

    /** Shuts down this client. */
    public void shutdown() {
        executorService.shutdown();
    }

    /** An authorization request job run by the execution service. */
    private class AuthorizationRequestJob implements Callable<Response> {

        /** The authorization request to be made. */
        private Request authzRequest;

        /**
         * Constructor.
         * 
         * @param request the request to send
         */
        public AuthorizationRequestJob(Request request) {
            authzRequest = request;
        }

        /** {@inheritDoc} */
        public Response call() throws Exception {
            log.debug("Processing authorization request: {}", authzRequest);
            
            daemonClient.runPolicyInformationPoints(authzRequest, clientConfig.getPolicyInformationPoints());
            
            Response response;
            for (String pepUrl : clientConfig.getPepDaemonEndpoints()) {
                response = daemonClient.performRequest(pepUrl, authzRequest);
                if (response != null) {
                    return response;
                }
            }

            return null;
        }
    }
}