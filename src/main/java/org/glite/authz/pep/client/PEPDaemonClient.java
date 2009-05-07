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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.glite.authz.common.AuthorizationServiceException;
import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Response;
import org.glite.authz.common.pip.PolicyInformationPoint;
import org.glite.authz.common.util.Base64;
import org.glite.authz.pep.client.config.PEPClientConfiguration;
import org.glite.voms.VOMSTrustManager;
import org.opensaml.ws.soap.client.http.HttpClientBuilder;
import org.opensaml.ws.soap.client.http.TLSProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/** A simple PEP daemon client. */
@ThreadSafe
public class PEPDaemonClient {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PEPDaemonClient.class);

    /** HTTP client used to contact the PEP daemon. */
    private HttpClient httpClient;

    /**
     * Constructor.
     * 
     * @param config the client configuration used for this client
     */
    public PEPDaemonClient(PEPClientConfiguration config) {
        HttpClientBuilder httpClientBuilder = new HttpClientBuilder();
        httpClientBuilder.setConnectionTimeout(config.getConnectionTimeout());
        httpClientBuilder.setMaxTotalConnections(config.getMaxRequests());
        httpClientBuilder.setReceiveBufferSize(config.getReceiveBufferSize());
        httpClientBuilder.setSendBufferSize(config.getSendBufferSize());

        if (config.getTrustMaterialStore() != null) {
            try {
                VOMSTrustManager trustManager = new VOMSTrustManager(config.getTrustMaterialStore());
                httpClientBuilder.setHttpsProtocolSocketFactory(new TLSProtocolSocketFactory(null, trustManager));
            } catch (Exception e) {
                log.error("Unable to create trust manager, SSL/TLS connections to PEP will not be supported");
            }
        }
        httpClient = httpClientBuilder.buildClient();
    }

    /**
     * Calls out to the remote PEP and returns the response.
     * 
     * @param pepUrl the remote PEP to which to callout
     * @param authzRequest the authorization request to send to the PEP daemon
     * 
     * @return the response to the request
     * 
     * @throws AuthorizationServiceException thrown if there is a problem processing the request
     */
    public Response performRequest(String pepUrl, Request authzRequest) throws AuthorizationServiceException {
        PostMethod postMethod = null;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HessianOutput hout = new HessianOutput(out);
            hout.writeObject(authzRequest);
            hout.flush();

            String b64Message = Base64.encodeBytes(out.toByteArray());
            log.debug("Outgoing Base64-encoded request:\n{}", b64Message);

            postMethod = new PostMethod(pepUrl);
            postMethod.setRequestEntity(new StringRequestEntity(b64Message, "UTF-8", "UTF-8"));
        } catch (IOException e) {
            log.error("Unable to serialize request object", e);
            throw new AuthorizationServiceException("Unable to serialize request object", e);
        }

        try {
            httpClient.executeMethod(postMethod);
            if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
                HessianInput hin = new HessianInput(new Base64.InputStream(postMethod.getResponseBodyAsStream()));
                return (Response) hin.readObject(Response.class);
            }
            log.error("Recieved a {} status code response, expected a 200, from the PEP daemon {}", postMethod
                    .getStatusCode(), pepUrl);
            return null;
        } catch (IOException e) {
            log.error("Unable to read response from PEP daemon " + pepUrl, e);
            throw new AuthorizationServiceException("Unable to read response from PEP daemon " + pepUrl, e);
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * Run the list of PIPs over the request.
     * 
     * @param request the request
     * @param pips PIPs to run over the request
     * 
     * @throws AuthorizationServiceException thrown if there is a
     */
    public void runPolicyInformationPoints(Request request, List<PolicyInformationPoint> pips)
            throws AuthorizationServiceException {
        // we copy this into a new list so that if changes are made to the list within the config we are not effected
        ArrayList<PolicyInformationPoint> pipsCopy = new ArrayList<PolicyInformationPoint>(pips);
        boolean pipApplied;

        log.debug("Running {} registered policy information points", pipsCopy.size());
        for (PolicyInformationPoint pip : pipsCopy) {
            if (pip != null) {
                pipApplied = pip.populateRequest(request);
                if (pipApplied) {
                    log.debug("PIP {} was applied to the request", pip.getId());
                } else {
                    log.debug("PIP {} did not apply to this request", pip.getId());
                }
            }
        }
    }

    /**
     * Runs the obligations handlers over the returned response.
     * 
     * @param request the authorization request made
     * @param response the authorization response
     * 
     * @throws AuthorizationServiceException thrown if there is a problem evaluating obligation handlers.
     */
    public void runObligationHandlers(Request request, Response response) throws AuthorizationServiceException {
        // TODO
    }
}