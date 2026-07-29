package cn.iocoder.yudao.module.store.job;

import cn.iocoder.yudao.module.store.dal.dataobject.clean.StoreCleanLogDO;
import cn.iocoder.yudao.module.store.service.clean.StoreCleanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 门店数据清洗定时任务
 *
 * 每天凌晨 2 点执行一次，清洗全部门户的门店数据。
 * 清洗规则详见 {@link StoreCleanServiceImpl#cleanForCurrentTenant()}
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class StoreCleanJob {

    /** 触发方式：定时任务 */
    private static final Integer TRIGGER_TYPE_SCHEDULED = 1;

    @Resource
    private StoreCleanService storeCleanService;

    /**
     * 每天凌晨 2 点执行门店数据清洗
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanStoreData() {
        log.info("[cleanStoreData][开始执行门店数据清洗定时任务]");
        StoreCleanLogDO cleanLog = storeCleanService.clean(TRIGGER_TYPE_SCHEDULED);
        log.info("[cleanStoreData][门店数据清洗定时任务完成：扫描数量={}, 修改数量={}, 状态={}]",
                cleanLog.getScanCount(), cleanLog.getModifyCount(), cleanLog.getStatus());
    }

}
