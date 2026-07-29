package cn.iocoder.yudao.module.store.service.clean;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.service.TenantFrameworkService;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.store.controller.admin.clean.vo.StoreCleanLogPageReqVO;
import cn.iocoder.yudao.module.store.dal.dataobject.clean.StoreCleanLogDO;
import cn.iocoder.yudao.module.store.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.store.dal.mysql.clean.StoreCleanLogMapper;
import cn.iocoder.yudao.module.store.dal.mysql.store.StoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 门店数据清洗 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class StoreCleanServiceImpl implements StoreCleanService {

    @Resource
    private StoreMapper storeMapper;
    @Resource
    private StoreCleanLogMapper storeCleanLogMapper;
    @Resource
    private TenantFrameworkService tenantFrameworkService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreCleanLogDO clean(Integer triggerType) {
        LocalDateTime startTime = LocalDateTime.now();
        // 累计统计：扫描数量、修改数量
        AtomicInteger scanCount = new AtomicInteger(0);
        AtomicInteger modifyCount = new AtomicInteger(0);
        String errorMsg = null;
        Integer status = 0; // 0=成功
        try {
            // 1. 获取全部启用租户
            List<Long> tenantIds = tenantFrameworkService.getTenantIds();
            if (CollUtil.isEmpty(tenantIds)) {
                log.info("[clean][没有启用的租户，跳过清洗]");
            } else {
                // 2. 逐个租户执行清洗
                for (Long tenantId : tenantIds) {
                    TenantUtils.execute(tenantId, () -> {
                        int[] result = cleanForCurrentTenant();
                        scanCount.addAndGet(result[0]);
                        modifyCount.addAndGet(result[1]);
                    });
                }
            }
        } catch (Exception e) {
            log.error("[clean][门店数据清洗发生异常]", e);
            status = 1; // 1=失败
            errorMsg = e.getMessage();
        }
        LocalDateTime endTime = LocalDateTime.now();
        // 3. 写入清洗日志（日志为系统级数据，忽略租户上下文写入）
        StoreCleanLogDO cleanLog = StoreCleanLogDO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .scanCount(scanCount.get())
                .modifyCount(modifyCount.get())
                .status(status)
                .errorMsg(errorMsg)
                .triggerType(triggerType)
                .build();
        TenantUtils.executeIgnore(() -> storeCleanLogMapper.insert(cleanLog));
        return cleanLog;
    }

    /**
     * 清洗当前租户的门店数据
     *
     * @return int[2]，int[0]=扫描数量，int[1]=修改数量
     */
    private int[] cleanForCurrentTenant() {
        // 1. 查询当前租户全部门店（不分页）
        List<StoreDO> list = storeMapper.selectList();
        int scanCount = list.size();
        int modifyCount = 0;
        for (StoreDO store : list) {
            // 2. 逐条清洗，只更新有变化的字段
            boolean changed = false;
            StoreDO updateObj = new StoreDO();
            updateObj.setId(store.getId());

            // 2.1 门店名称：去前后空格
            if (store.getStoreName() != null) {
                String trimmed = store.getStoreName().trim();
                if (!trimmed.equals(store.getStoreName())) {
                    updateObj.setStoreName(trimmed);
                    changed = true;
                }
            }
            // 2.2 负责人：去前后空格
            if (store.getManager() != null) {
                String trimmed = store.getManager().trim();
                if (!trimmed.equals(store.getManager())) {
                    updateObj.setManager(trimmed);
                    changed = true;
                }
            }
            // 2.3 联系电话：去空格和短横线
            if (store.getPhone() != null) {
                String cleaned = StrUtil.removeAll(store.getPhone(), ' ', '-');
                if (!cleaned.equals(store.getPhone())) {
                    updateObj.setPhone(cleaned);
                    changed = true;
                }
            }
            // 2.4 平台：统一名称
            if (store.getPlatform() != null) {
                String normalized = normalizePlatform(store.getPlatform());
                if (!normalized.equals(store.getPlatform())) {
                    updateObj.setPlatform(normalized);
                    changed = true;
                }
            }
            // 3. 有变化才更新
            if (changed) {
                storeMapper.updateById(updateObj);
                modifyCount++;
            }
        }
        return new int[]{scanCount, modifyCount};
    }

    /**
     * 平台名称统一：
     * meituan、美 团 → 美团
     * eleme、饿了么平台 → 饿了么
     * jd、京东到家 → 京东
     */
    private String normalizePlatform(String platform) {
        if (platform == null) {
            return null;
        }
        String trimmed = platform.trim();
        // 全部小写匹配，避免大小写差异
        switch (trimmed.toLowerCase()) {
            case "meituan":
            case "美团":
            case "美 团":
                return "美团";
            case "eleme":
            case "饿了么":
            case "饿了么平台":
                return "饿了么";
            case "jd":
            case "京东":
            case "京东到家":
                return "京东";
            default:
                return trimmed;
        }
    }

    @Override
    public PageResult<StoreCleanLogDO> getCleanLogPage(StoreCleanLogPageReqVO pageReqVO) {
        return storeCleanLogMapper.selectPage(pageReqVO);
    }

}
