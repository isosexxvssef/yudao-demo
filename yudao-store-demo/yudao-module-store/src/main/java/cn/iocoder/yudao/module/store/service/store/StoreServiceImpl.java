package cn.iocoder.yudao.module.store.service.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import cn.iocoder.yudao.module.store.controller.admin.store.vo.*;
import cn.iocoder.yudao.module.store.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.store.dal.mysql.store.StoreMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.store.enums.ErrorCodeConstants.*;

/**
 * 门店 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class StoreServiceImpl implements StoreService {

    @Resource
    private StoreMapper storeMapper;

    @Override
    public Long createStore(StoreSaveReqVO createReqVO) {
        // 校验门店编码租户内唯一
        validateStoreCodeUnique(null, createReqVO.getStoreCode());
        // 插入
        StoreDO store = BeanUtils.toBean(createReqVO, StoreDO.class);
        storeMapper.insert(store);
        // 返回
        return store.getId();
    }

    @Override
    public void updateStore(StoreSaveReqVO updateReqVO) {
        // 校验存在
        validateStoreExists(updateReqVO.getId());
        // 校验门店编码租户内唯一
        validateStoreCodeUnique(updateReqVO.getId(), updateReqVO.getStoreCode());
        // 更新
        StoreDO updateObj = BeanUtils.toBean(updateReqVO, StoreDO.class);
        storeMapper.updateById(updateObj);
    }

    @Override
    public void deleteStore(Long id) {
        // 校验存在
        validateStoreExists(id);
        // 删除
        storeMapper.deleteById(id);
    }

    @Override
    public void deleteStoreListByIds(List<Long> ids) {
        // 删除
        storeMapper.deleteByIds(ids);
    }

    private void validateStoreExists(Long id) {
        if (storeMapper.selectById(id) == null) {
            throw exception(STORE_NOT_EXISTS);
        }
    }

    /**
     * 校验门店编码在当前租户内唯一
     *
     * @param id 门店编号（更新时传入，排除自身）
     * @param storeCode 门店编码
     */
    private void validateStoreCodeUnique(Long id, String storeCode) {
        if (StrUtil.isBlank(storeCode)) {
            return;
        }
        StoreDO store = storeMapper.selectByStoreCode(storeCode);
        if (store == null) {
            return;
        }
        if (ObjUtil.notEqual(store.getId(), id)) {
            throw exception(STORE_CODE_DUPLICATE);
        }
    }

    @Override
    public StoreDO getStore(Long id) {
        return storeMapper.selectById(id);
    }

    @Override
    public PageResult<StoreDO> getStorePage(StorePageReqVO pageReqVO) {
        return storeMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreImportRespVO importStoreList(List<StoreImportExcelVO> importStores, boolean updateSupport) {
        // 1. 参数校验
        if (CollUtil.isEmpty(importStores)) {
            throw exception(STORE_IMPORT_LIST_IS_EMPTY);
        }

        // 2. 遍历，逐个创建 or 更新（导入的数据自动归属当前登录租户，由多租户拦截器自动注入 tenant_id）
        StoreImportRespVO respVO = StoreImportRespVO.builder()
                .createCodes(new ArrayList<>()).updateCodes(new ArrayList<>())
                .failureCodes(new LinkedHashMap<>()).build();
        AtomicInteger index = new AtomicInteger(1);
        importStores.forEach(importStore -> {
            int currentIndex = index.getAndIncrement();
            // 2.1 校验字段
            String key = StrUtil.blankToDefault(importStore.getStoreCode(), "第 " + currentIndex + " 行");
            if (StrUtil.isBlank(importStore.getStoreCode())) {
                respVO.getFailureCodes().put(key, "门店编码不能为空");
                return;
            }
            if (StrUtil.isBlank(importStore.getStoreName())) {
                respVO.getFailureCodes().put(key, "门店名称不能为空");
                return;
            }

            // 2.2 判断：创建 or 更新
            StoreDO existStore = storeMapper.selectByStoreCode(importStore.getStoreCode());
            if (existStore == null) {
                // 2.2.1 创建
                StoreDO store = BeanUtils.toBean(importStore, StoreDO.class);
                storeMapper.insert(store);
                respVO.getCreateCodes().add(importStore.getStoreCode());
            } else if (updateSupport) {
                // 2.2.2 更新
                StoreDO updateObj = BeanUtils.toBean(importStore, StoreDO.class);
                updateObj.setId(existStore.getId());
                storeMapper.updateById(updateObj);
                respVO.getUpdateCodes().add(importStore.getStoreCode());
            } else {
                // 不支持更新
                respVO.getFailureCodes().put(key, "门店编码已存在");
            }
        });
        return respVO;
    }

}
