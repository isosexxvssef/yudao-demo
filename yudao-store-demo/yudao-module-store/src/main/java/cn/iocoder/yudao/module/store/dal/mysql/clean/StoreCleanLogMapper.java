package cn.iocoder.yudao.module.store.dal.mysql.clean;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.store.controller.admin.clean.vo.StoreCleanLogPageReqVO;
import cn.iocoder.yudao.module.store.dal.dataobject.clean.StoreCleanLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店数据清洗日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface StoreCleanLogMapper extends BaseMapperX<StoreCleanLogDO> {

    default PageResult<StoreCleanLogDO> selectPage(StoreCleanLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StoreCleanLogDO>()
                .eqIfPresent(StoreCleanLogDO::getStatus, reqVO.getStatus())
                .eqIfPresent(StoreCleanLogDO::getTriggerType, reqVO.getTriggerType())
                .betweenIfPresent(StoreCleanLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StoreCleanLogDO::getId));
    }

}
