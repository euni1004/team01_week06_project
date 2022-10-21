package com.week06.team01_week06_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalResDto<T> {

    private boolean success;
    private Error error;


    public static <T> GlobalResDto<T> success() {
        return new GlobalResDto<>(true, null);
    }

    public static <T> GlobalResDto<T> fail(String code, String meg) {
        return new GlobalResDto<>(false, new Error(code, meg));
    }
}
