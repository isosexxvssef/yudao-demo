package cn.iocoder.yudao.module.store.dal.mysql.store;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.store.dal.dataobject.store.StoreDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.store.controller.admin.store.vo.*;

/**
 * 门店 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface StoreMapper extends BaseMapperX<StoreDO> {

    default StoreDO selectByStoreCode(String storeCode) {
        return selectOne(StoreDO::getStoreCode, storeCode);
    }

    default PageResult<StoreDO> selectPage(StorePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StoreDO>()
                .eqIfPresent(StoreDO::getStoreCode, reqVO.getStoreCode())
                .likeIfPresent(StoreDO::getStoreName, reqVO.getStoreName())
                .eqIfPresent(StoreDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(StoreDO::getCity, reqVO.getCity())
                .likeIfPresent(StoreDO::getManager, reqVO.getManager())
                .eqIfPresent(StoreDO::getPhone, reqVO.getPhone())
                .eqIfPresent(StoreDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StoreDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StoreDO::getId));
    }

}
