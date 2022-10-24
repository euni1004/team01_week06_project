package com.week06.team01_week06_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalResDto<T> {

    private boolean success;
    private T data;


    public static <T> GlobalResDto<T> success(T data) {
        return new GlobalResDto<>(true,data);
    }


}
