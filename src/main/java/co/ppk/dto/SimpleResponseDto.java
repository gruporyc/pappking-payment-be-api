package co.ppk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleResponseDto {

    private boolean success;
    private String message;

    public SimpleResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SimpleResponseDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
