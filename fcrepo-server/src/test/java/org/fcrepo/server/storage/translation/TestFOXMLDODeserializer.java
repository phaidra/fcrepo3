/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */

package org.fcrepo.server.storage.translation;

import org.fcrepo.server.storage.translation.DODeserializer;
import org.fcrepo.server.storage.translation.DOSerializer;

/**
 * Common unit tests for FOXML deserializers.
 *
 * @author Chris Wilper
 */
public abstract class TestFOXMLDODeserializer
        extends TestXMLDODeserializer {

    TestFOXMLDODeserializer(DODeserializer deserializer,
                            DOSerializer associatedSerializer) {
        super(deserializer, associatedSerializer);
    }

}
