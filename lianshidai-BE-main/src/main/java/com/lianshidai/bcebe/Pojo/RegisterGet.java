package com.lianshidai.bcebe.Pojo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@Validated
public class RegisterGet {
    @NotNull
    @Valid
    private User user;

    @NotBlank
    @Size(min = 6,max = 6,message = "验证码长度为6")
    private String code;
}
