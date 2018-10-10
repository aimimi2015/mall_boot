package com.mall_boot.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    @NotBlank(message = "姓名必填")
    private String username;

    @JsonIgnore
    @NotBlank(message = "密码必填")
    private String password;

    @NotBlank(message = "邮箱必填")
    private String email;

    @NotBlank(message = "电话必填")
    private String phone;

    @NotBlank(message = "问题必填")
    private String question;

    @JsonIgnore
    @NotBlank(message = "答案必填")
    private String answer;

    private Integer role;

    private Date createTime;

    private Date updateTime;
}