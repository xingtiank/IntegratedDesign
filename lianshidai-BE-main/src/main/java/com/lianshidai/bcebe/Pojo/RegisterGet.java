package com.lianshidai.bcebe.Pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterGet {
    @NotNull
    private User user;

    @NotBlank
    @Size(min = 6,max = 6,message = "验证码长度为6")
    private String code;
}
