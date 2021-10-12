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
@TableName("data_obj")
@ApiModel(value="DataObj对象", description="")
public class DataObj implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "数据对象key")
    @TableField("data_obj_key")
    private String dataObjKey;

    @ApiModelProperty(value = "数据对象名称")
    @TableField("data_obj_name")
    private String dataObjName;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_delete")
    private Integer isDelete;


}
