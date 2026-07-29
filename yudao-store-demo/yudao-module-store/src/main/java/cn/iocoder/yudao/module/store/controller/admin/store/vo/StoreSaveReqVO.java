package cn.iocoder.yudao.module.store.controller.admin.store.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 门店新增/修改 Request VO")
@Data
public class StoreSaveReqVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "22320")
    private Long id;

    @Schema(description = "门店编码，租户内唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "门店编码，租户内唯一不能为空")
    private String storeCode;

    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "门店名称不能为空")
    private String storeName;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "营业状态（营业/停业）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "营业状态（营业/停业）不能为空")
    private String status;

}