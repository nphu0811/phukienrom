package com.example.demo.dto.response;
import java.time.LocalDateTime;

public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    public ApiResponse() {}
    public ApiResponse(LocalDateTime timestamp, int status, String message, T data) {
        this.timestamp = timestamp; this.status = status; this.message = message; this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(LocalDateTime.now(), 200, "Success", data);
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(LocalDateTime.now(), 200, message, data);
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(LocalDateTime.now(), 201, "Created successfully", data);
    }
    public static ApiResponse<Void> error(int status, String message) {
        return new ApiResponse<>(LocalDateTime.now(), status, message, null);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
