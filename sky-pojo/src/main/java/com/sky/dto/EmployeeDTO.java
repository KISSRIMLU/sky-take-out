package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工数据传输对象")
public class EmployeeDTO implements Serializable {

    @ApiModelProperty(value = "员工ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "姓名", required = true, example = "张三")
    private String name;

    @ApiModelProperty(value = "手机号", required = true, example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "性别", required = true, example = "1", notes = "1:男 2:女")
    private String sex;

    @ApiModelProperty(value = "身份证号", required = true, example = "11010119900307663X")
    private String idNumber;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;  // 注意：你的DTO里没有password字段，但Controller需要
}