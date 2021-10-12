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
@TableName("field")
@ApiModel(value="Field对象", description="")
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字段所属数据对象")
    @TableField("data_obj_id")
    private Long dataObjId;

    @ApiModelProperty(value = "字段key")
    @TableField("field_key")
    private String fieldKey;

    @ApiModelProperty(value = "字段名称")
    @TableField("field_name")
    private String fieldName;

    @ApiModelProperty(value = "字段数据类型")
    @TableField("field_type")
    private String fieldType;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_delete")
    private Integer isDelete;


}
