package com.example.courseprojectapi.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    private Integer status;
    private String error;
    private String path;
    private LocalDateTime timestamp;

    public static <T> BaseResponse<T> success(String message, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static BaseResponse<Object> error(int status, String error, String message, String path) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setSuccess(false);
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
