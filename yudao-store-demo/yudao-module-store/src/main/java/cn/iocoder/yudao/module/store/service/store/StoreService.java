package cn.iocoder.yudao.module.store.service.store;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.store.controller.admin.store.vo.*;
import cn.iocoder.yudao.module.store.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 门店 Service 接口
 *
 * @author 芋道源码
 */
public interface StoreService {

    /**
     * 创建门店
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStore(@Valid StoreSaveReqVO createReqVO);

    /**
     * 更新门店
     *
     * @param updateReqVO 更新信息
     */
    void updateStore(@Valid StoreSaveReqVO updateReqVO);

    /**
     * 删除门店
     *
     * @param id 编号
     */
    void deleteStore(Long id);

    /**
    * 批量删除门店
    *
    * @param ids 编号
    */
    void deleteStoreListByIds(List<Long> ids);

    /**
     * 获得门店
     *
     * @param id 编号
     * @return 门店
     */
    StoreDO getStore(Long id);

    /**
     * 获得门店分页
     *
     * @param pageReqVO 分页查询
     * @return 门店分页
     */
    PageResult<StoreDO> getStorePage(StorePageReqVO pageReqVO);

    /**
     * 导入门店数据
     *
     * @param importStores 导入的门店列表
     * @param updateSupport 是否支持更新
     * @return 导入结果
     */
    StoreImportRespVO importStoreList(List<StoreImportExcelVO> importStores, boolean updateSupport);

}
