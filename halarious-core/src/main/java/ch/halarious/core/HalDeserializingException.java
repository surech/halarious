/*
 * Copyright 2014 Stefan Urech
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *    limitations under the License
 */

package ch.halarious.core;

/**
 * Wird bei Fehler beim Deserialisieren von HAL-Objekten ausgelöst
 *
 * @author surech
 */
public class HalDeserializingException extends RuntimeException {

    public HalDeserializingException() {
    }

    public HalDeserializingException(String message) {
        super(message);
    }

    public HalDeserializingException(String message, Throwable cause) {
        super(message, cause);
    }

    public HalDeserializingException(Throwable cause) {
        super(cause);
    }

    public HalDeserializingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
