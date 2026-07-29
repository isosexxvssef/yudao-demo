package cn.iocoder.yudao.module.store.controller.admin.clean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 门店清洗日志 Response VO")
@Data
public class StoreCleanLogRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "扫描数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer scanCount;

    @Schema(description = "修改数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer modifyCount;

    @Schema(description = "执行状态（0=成功 1=失败）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "触发方式（1=定时任务 2=手动触发）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer triggerType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
