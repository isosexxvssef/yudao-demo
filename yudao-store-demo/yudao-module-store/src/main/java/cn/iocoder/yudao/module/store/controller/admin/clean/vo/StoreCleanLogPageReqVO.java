package cn.iocoder.yudao.module.store.controller.admin.clean.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 门店清洗日志分页 Request VO")
@Data
public class StoreCleanLogPageReqVO extends PageParam {

    @Schema(description = "执行状态（0=成功 1=失败）")
    private Integer status;

    @Schema(description = "触发方式（1=定时任务 2=手动触发）")
    private Integer triggerType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
