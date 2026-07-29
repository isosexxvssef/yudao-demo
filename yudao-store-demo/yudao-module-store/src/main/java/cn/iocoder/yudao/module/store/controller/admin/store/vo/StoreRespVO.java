package cn.iocoder.yudao.module.store.controller.admin.store.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 门店 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StoreRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22320")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "门店编码，租户内唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("门店编码，租户内唯一")
    private String storeCode;

    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("门店名称")
    private String storeName;

    @Schema(description = "平台")
    @ExcelProperty("平台")
    private String platform;

    @Schema(description = "城市")
    @ExcelProperty("城市")
    private String city;

    @Schema(description = "负责人")
    @ExcelProperty("负责人")
    private String manager;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "营业状态（营业/停业）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "营业状态（营业/停业）", converter = DictConvert.class)
    @DictFormat("store_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}