package cn.iocoder.yudao.module.store.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
/**
 * 门店模块错误码常量
 */
public interface ErrorCodeConstants {

    // ========== 门店 ==========
    ErrorCode STORE_NOT_EXISTS = new ErrorCode(1_001_000_001, "门店不存在");
    ErrorCode STORE_CODE_DUPLICATE = new ErrorCode(1_001_000_002, "门店编码已存在");
    ErrorCode STORE_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_001_000_003, "导入门店数据不能为空");

}
