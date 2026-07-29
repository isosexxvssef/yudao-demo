package cn.iocoder.yudao.module.store.controller.admin.store.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 门店分页 Request VO")
@Data
public class StorePageReqVO extends PageParam {

    @Schema(description = "门店编码，租户内唯一")
    private String storeCode;

    @Schema(description = "门店名称", example = "芋艿")
    private String storeName;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "营业状态（营业/停业）")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}