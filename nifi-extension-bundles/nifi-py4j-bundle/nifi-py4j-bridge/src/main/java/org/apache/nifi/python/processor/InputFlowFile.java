/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nifi.python.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public interface InputFlowFile {

    /**
     * Returns the contents of FlowFile as a byte array.
     * @return a byte array representation of the FlowFile
     * @throws IOException if FlowFile content is too large to load into a byte array or if unable to access the FlowFile's content
     */
    byte[] getContentsAsBytes() throws IOException;

    /**
     * Returns a BufferedReader that wraps the FlowFile's content. This allows a single line of text to be read at a time,
     * rather than buffering the entirety of the FlowFile's content into memory.
     * @return a BufferedReader that provides access to the FlowFile content
     * @throws IOException if unable to access the FlowFile's content
     */
    BufferedReader getContentsAsReader() throws IOException;

    /**
     * @return the size, in bytes, of the FlowFile's content
     */
    long getSize();

    /**
     * Returns the value of the attribute with the given name, or <code>null</code> if no attribute exists with that name
     * @param name the name of the attribute
     * @return the value of the attribute, or <code>null</code> if no attribute exists with that name
     */
    String getAttribute(final String name);

    /**
     * @return A map of FlowFile attribute names to values
     */
    Map<String, String> getAttributes();

}
