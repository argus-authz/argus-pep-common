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

package org.glite.authz.common.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.glite.authz.common.model.util.LazySet;

/** An authorization request. */
public final class Request implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7263488106115027959L;

    /** Subjects about which the request is being made. */
    private Set<Subject> subjects;

    /** Resources about which the request is being made. */
    private Set<Resource> resources;

    /** The action to be authorized. */
    private Action action;

    /** The environment within which the request is being made. */
    private Environment environment;

    /** Constructor. */
    public Request() {
        subjects = new LazySet<Subject>();
        resources = new LazySet<Resource>();
    }

    /**
     * Gets the subject about which the request is being made.
     * 
     * @return subject about which the request is being made
     */
    public Set<Subject> getSubjects() {
        return subjects;
    }

    /**
     * Gets the resources about which the request is being made.
     * 
     * @return resources about which the request is being made
     */
    public Set<Resource> getResources() {
        return resources;
    }

    /**
     * Gets the action about which the request is being made.
     * 
     * @return action about which the request is being made
     */
    public Action getAction() {
        return action;
    }

    /**
     * Sets the action about which the request is being made.
     * 
     * @param newAction action about which the request is being made
     */
    public void setAction(Action newAction) {
        action = newAction;
    }

    /**
     * Gets the environment about which the request is being made.
     * 
     * @return environment about which the request is being made
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment about which the request is being made.
     * 
     * @param newEnvironment environment about which the request is being made
     */
    public void setEnvironment(Environment newEnvironment) {
        environment = newEnvironment;
    }

    /** {@inheritDoc} */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Request {");
        stringBuilder.append("subjects: [");
        Iterator<Subject> iterator= subjects.iterator();
        while (iterator.hasNext()) {
            Subject subject = (Subject) iterator.next();
            stringBuilder.append(subject);
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("], ");
        stringBuilder.append("resources: [");
        Iterator<Resource> it= resources.iterator();
        while (it.hasNext()) {
            Resource resource = (Resource) it.next();
            stringBuilder.append(resource);
            if (it.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("], ");
        stringBuilder.append("action: ").append(action).append(", ");
        stringBuilder.append("environment: ").append(environment);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int hash = 13;

        hash = 31 * hash + (null == action ? 0 : action.hashCode());
        hash = 31 * hash + (null == environment ? 0 : environment.hashCode());
        hash = 31 * hash + resources.hashCode();
        hash = 31 * hash + subjects.hashCode();

        return hash;
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Request otherRequest = (Request) obj;

        boolean actionsEqual;
        if (action == null) {
            if (otherRequest.getAction() == null) {
                actionsEqual = true;
            } else {
                actionsEqual = false;
            }
        } else {
            actionsEqual = action.equals(otherRequest.getAction());
        }

        boolean environmentsEqual;
        if (environment == null) {
            if (otherRequest.getEnvironment() == null) {
                environmentsEqual = true;
            } else {
                environmentsEqual = false;
            }
        } else {
            environmentsEqual = environment.equals(otherRequest.getEnvironment());
        }

        return actionsEqual && environmentsEqual && resources.equals(otherRequest.getResources())
                && subjects.equals(otherRequest.getSubjects());
    }
}