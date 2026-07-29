package cn.iocoder.yudao.module.store.controller.admin.store.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.store.enums.DictTypeConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店 Excel 导入 VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreImportExcelVO {

    @ExcelProperty("门店编码")
    private String storeCode;

    @ExcelProperty("门店名称")
    private String storeName;

    @ExcelProperty("平台")
    private String platform;

    @ExcelProperty("城市")
    private String city;

    @ExcelProperty("负责人")
    private String manager;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty(value = "营业状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.STORE_STATUS)
    private String status;

}
