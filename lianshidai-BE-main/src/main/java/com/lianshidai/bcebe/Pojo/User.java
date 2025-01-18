package com.lianshidai.bcebe.Pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Validated
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度在3-20之间")
    private String username;

    @NotNull(message = "姓名不能为空")
    @Size(min = 1, max = 10, message = "姓名过长")
    private String name;

    @NotNull(message = "学号不能为空")
    @TableField(value = "student_id")
    private  Long StudentID;

    @NotNull(message = "电话不能为空")
    private Long phone;

    @NotNull(message = "密码不能为空")
    @Size(min = 10, max = 20, message = "密码长度在10-20之间")
    private String password;

    @NotNull(message = "邮箱不能为空")
    @Size(min = 5, max = 30, message = "邮箱无效")
    private String email;

    private Integer status;

    private Integer scoreTotal;
    private Integer scoreBe;
    private Integer scoreFe;
    private Integer scoreMl;
    private Integer scoreBc;
}
