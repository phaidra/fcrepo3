/*
 * File: AuthzDeniedException.java
 *
 * Copyright 2007 Macquarie E-Learning Centre Of Excellence
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.server.security.xacml.pep;

/**
 * @author nishen@melcoe.mq.edu.au
 */
public class AuthzDeniedException
        extends Exception {

    private static final long serialVersionUID = 9156316947565968757L;

    public AuthzDeniedException() {
        super();
    }

    public AuthzDeniedException(String message) {
        super(message);
    }

    public AuthzDeniedException(Throwable cause) {
        super(cause);
    }

    public AuthzDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
