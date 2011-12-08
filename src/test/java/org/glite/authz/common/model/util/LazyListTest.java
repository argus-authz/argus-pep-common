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

import java.util.List;

import junit.framework.TestCase;

/**
 * The class <code>LazyListTest</code> contains tests for the class {@link
 * <code>LazyList</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 11/25/11 4:38 PM
 *
 * @author Valery Tschopp &lt;valery.tschopp&#64;switch.ch&gt;
 *
 * @version $Revision$
 */
public class LazyListTest extends TestCase {

    /**
     * Perform pre-test initialization
     *
     * @throws Exception
     *
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Perform post-test clean up
     *
     * @throws Exception
     *
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsEmpty() {
        List<String> list= new LazyList<String>();
        assertTrue(list.isEmpty());
    }
}

