package cn.iocoder.yudao.module.store.dal.dataobject.clean;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 门店数据清洗日志 DO
 *
 * 清洗日志为系统级数据（跨租户），不参与多租户隔离。
 *
 * @author 芋道源码
 */
@TableName("zy_store_clean_log")
@KeySequence("zy_store_clean_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCleanLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 扫描数量
     */
    private Integer scanCount;
    /**
     * 修改数量
     */
    private Integer modifyCount;
    /**
     * 执行状态
     *
     * 0=成功，1=失败
     */
    private Integer status;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 触发方式
     *
     * 1=定时任务，2=手动触发
     */
    private Integer triggerType;

}
