package org.rjansen.sdk.exceptions;

import java.util.Objects;


public class MessageWrapper {
    private String level;
    private String message;

    private MessageWrapper() {

    }

    public static MessageWrapper wrapInfo(Exception ex) {
        MessageWrapper wrapper = new MessageWrapper();
        wrapper.level = "INFO";
        wrapper.message = ex.getMessage();
        return wrapper;
    }

    public static MessageWrapper wrapWarning(Exception ex) {
        MessageWrapper wrapper = new MessageWrapper();
        wrapper.level = "WARN";
        wrapper.message = ex.getMessage();
        return wrapper;
    }

    public static MessageWrapper wrapError(Exception ex) {
        MessageWrapper wrapper = new MessageWrapper();
        wrapper.level = "ERROR";
        wrapper.message = ex.getMessage();
        return wrapper;
    }

    public String getLevel() {
        return level;
    }


    public String getMessage() {
        return message;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageWrapper that = (MessageWrapper) o;
        return Objects.equals(level, that.level) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, message);
    }

    public String toJson() {
        return "{" +
                "\"level\":" + level +
                "\"message\":" + message +
                "}";
    }
}
