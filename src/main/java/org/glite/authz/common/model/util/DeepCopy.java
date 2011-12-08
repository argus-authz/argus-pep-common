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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects.
 * Objects are first serialized and then deserialized. Copied objects must
 * implement the {@link Serializable} interface.
 * <p>
 * Error checking is fairly minimal in this implementation. If an object is
 * encountered that cannot be serialized (or that references an object that
 * cannot be serialized) an error is printed to System.err and null is returned.
 * <p>
 * <b>NOTICE:</b> The performance of this deep copy utility is not optimal,
 * serializing and deserializing objects is a very time consuming operation.
 */
public class DeepCopy {

    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     * 
     * @param object
     *            to deep copy
     * @return a deep copy of the original object or <code>null</code> if an
     *         error occurs.
     */
    public static Object copy(Object object) {
        Object copy= null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            ObjectOutputStream oos= new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            oos.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ByteArrayInputStream bais= new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois= new ObjectInputStream(bais);
            copy= ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return copy;
    }

}