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
