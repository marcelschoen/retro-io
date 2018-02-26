package org.retro.common;

public class VirtualDiskException extends Exception {
    public VirtualDiskException() {
    }

    public VirtualDiskException(String message) {
        super(message);
    }

    public VirtualDiskException(String message, Throwable cause) {
        super(message, cause);
    }

    public VirtualDiskException(Throwable cause) {
        super(cause);
    }

    public VirtualDiskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
