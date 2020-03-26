package com.webank.webase.sign.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseQueryParam {
    private Integer start;
    private Integer pageSize;
}