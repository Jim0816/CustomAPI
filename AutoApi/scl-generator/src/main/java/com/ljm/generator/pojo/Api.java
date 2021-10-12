package com.ljm.generator.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ljm
 * @since 2021-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("api")
@ApiModel(value="Api对象", description="")
public class Api implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "api名称描述")
    @TableField("api_name")
    private String apiName;

    @ApiModelProperty(value = "api地址")
    @TableField("api_url")
    private String apiUrl;

    @ApiModelProperty(value = "api操作数据对象id")
    @TableField("data_obj_id")
    private Long dataObjId;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_delete")
    private Integer isDelete;


}
