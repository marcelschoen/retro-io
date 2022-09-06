/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
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
package org.retro.common;

/**
 * Exception thrown by some operations related to dealing
 * with virtual floppy disk images.
 *
 * @author Marcel Schoen
 */
public class VirtualDiskException extends Exception {

    /**
     * Default constructor.
     */
    public VirtualDiskException() {
    }

    /**
     * Creates an exception with a message.
     *
     * @param message The exception message.
     */
    public VirtualDiskException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a cause and a message.
     *
     * @param message The exception message.
     * @param cause The causing throwable.
     */
    public VirtualDiskException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with a cause.
     *
     * @param cause The causing throwable.
     */
    public VirtualDiskException(Throwable cause) {
        super(cause);
    }
}
