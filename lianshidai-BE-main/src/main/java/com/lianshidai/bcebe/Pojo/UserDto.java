package com.lianshidai.bcebe.Pojo;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDto {
    @NotNull
    private Integer id;
    @NotNull
    private String username;
    @NotNull
    private String name;
    @NotNull
    private  Long StudentID;
    @NotNull
    private Long phone;
    @NotNull
    private String email;
    private Integer status;
}
