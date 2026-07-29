package cn.iocoder.yudao.module.store.service.clean;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.store.controller.admin.clean.vo.StoreCleanLogPageReqVO;
import cn.iocoder.yudao.module.store.dal.dataobject.clean.StoreCleanLogDO;

/**
 * 门店数据清洗 Service 接口
 *
 * @author 芋道源码
 */
public interface StoreCleanService {

    /**
     * 执行门店数据清洗
     *
     * @param triggerType 触发方式（1=定时任务 2=手动触发）
     * @return 清洗日志
     */
    StoreCleanLogDO clean(Integer triggerType);

    /**
     * 获得清洗日志分页
     *
     * @param pageReqVO 分页查询
     * @return 清洗日志分页
     */
    PageResult<StoreCleanLogDO> getCleanLogPage(StoreCleanLogPageReqVO pageReqVO);

}
