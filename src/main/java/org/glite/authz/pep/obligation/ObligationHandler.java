/*
 * Copyright (c) Members of the EGEE Collaboration. 2006-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
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
package org.glite.authz.pep.obligation;

import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Result;

/**
 * Obligation Handler interface
 * 
 * <p>Obligation handlers <strong>must</strong> be stateless.
 */
public interface ObligationHandler {

    /**
     * Gets a unique identifier (name) for this obligation handle.
     * 
     * @return unique identifier for this obligation handle
     */
    public String getId();

    /**
     * Gets the ID of the handled obligation.
     * 
     * @return ID of the handled obligation
     */
    public String getObligationId();

    /**
     * Evaluates the obligation represented by this handler.
     * 
     * @param request
     *            the authorization request
     * @param result
     *            the result being processed
     * @return <code>true</code> if the obligation handler applied to the result
     * @throws ObligationProcessingException
     *             thrown if there is a problem processing this handler
     */
    public boolean evaluateObligation(Request request, Result result)
            throws ObligationProcessingException;

    /**
     * Gets the precedence of the handler.
     * 
     * @return precedence of the handler
     */
    public int getHandlerPrecedence();
}
