package com.lianshidai.bcebe.Pojo;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String name;
    private  Long StudentID;
    private Long phone;
    private String email;
    private Integer status;
}
