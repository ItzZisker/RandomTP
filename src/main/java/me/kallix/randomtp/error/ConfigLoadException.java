package me.kallix.randomtp.error;

public final class ConfigLoadException extends RuntimeException {
    public ConfigLoadException(String reason) {
        super(reason);
    }
    public ConfigLoadException(Throwable throwable) {
        super(throwable);
    }
}
