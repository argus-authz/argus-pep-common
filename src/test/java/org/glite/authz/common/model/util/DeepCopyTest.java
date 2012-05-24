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
package org.glite.authz.common.model.util;

import org.glite.authz.common.model.Attribute;
import org.glite.authz.common.model.Subject;

import junit.framework.TestCase;

/**
 * The class <code>DeepCopyTest</code> contains tests for the class {@link <code>DeepCopy</code>}
 * 
 * @pattern JUnit Test Case
 * 
 * @generatedBy CodePro at 11/18/11 3:58 PM
 * 
 * @author Valery Tschopp &lt;valery.tschopp&#64;switch.ch&gt;
 * 
 * @version $Revision$
 */
public class DeepCopyTest extends TestCase {

    static final String DEFAULT_ATTRIBUTE_ID= "x-urn:test:attribute:default";

    private Attribute attribute_;

    private Subject subject_;

    /**
     * Construct new test instance
     * 
     * @param name
     *            the test name
     */
    public DeepCopyTest(String name) {
        super(name);
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        attribute_= newAttribute();
        subject_= newSubject();

    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAttributeDeepCopy() {
        Attribute copy= (Attribute) DeepCopy.copy(attribute_);
        assertEquals(copy, attribute_);
        // modify copy
        copy.setIssuer("you");
        copy.getValues().add("x-urn:another:attribute");
        assertFalse(copy.equals(attribute_));

    }

    public void testSubjectDeepCopy() {
        Subject copy= (Subject) DeepCopy.copy(subject_);
        assertEquals(copy, subject_);
        // modify copy and create same value
        copy.getAttributes().clear();
        copy.getAttributes().add(newAttribute());
        assertEquals(copy, subject_);
        // modify
        copy.getAttributes().add(newAttribute("x-urn:test:attribute:new"));
        assertFalse(copy.equals(subject_));
    }

    private Attribute newAttribute() {
        return newAttribute(DEFAULT_ATTRIBUTE_ID);
    }

    private Attribute newAttribute(String id) {
        Attribute attribute= new Attribute();
        attribute.setDataType(Attribute.DT_ANY_URI);
        attribute.setId(id);
        attribute.setIssuer("me");
        attribute.getValues().add("http://www.test.org");
        attribute.getValues().add("x-urn:test:value:1");
        return attribute;
    }

    private Subject newSubject() {
        Subject subject= new Subject();
        subject.setCategory("x-urn:test:category");
        subject.getAttributes().add(newAttribute());
        return subject;
    }

}
