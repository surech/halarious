/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
