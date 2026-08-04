package cn.iocoder.yudao.module.store.controller.admin.store;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.store.controller.admin.store.vo.*;
import cn.iocoder.yudao.module.store.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.store.service.store.StoreService;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理后台 - 门店")
@RestController
@RequestMapping("/store/store")
@Validated
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("/create")
    @Operation(summary = "创建门店")
    @PreAuthorize("@ss.hasPermission('store:store:create')")
    public CommonResult<Long> createStore(@Valid @RequestBody StoreSaveReqVO createReqVO) {
        return success(storeService.createStore(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新门店")
    @PreAuthorize("@ss.hasPermission('store:store:update')")
    public CommonResult<Boolean> updateStore(@Valid @RequestBody StoreSaveReqVO updateReqVO) {
        storeService.updateStore(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除门店")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('store:store:delete')")
    public CommonResult<Boolean> deleteStore(@RequestParam("id") Long id) {
        storeService.deleteStore(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除门店")
                @PreAuthorize("@ss.hasPermission('store:store:delete')")
    public CommonResult<Boolean> deleteStoreList(@RequestParam("ids") List<Long> ids) {
        storeService.deleteStoreListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得门店")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('store:store:query')")

    public CommonResult<StoreRespVO> getStore(@RequestParam("id") Long id) {
        StoreDO store = storeService.getStore(id);
        return success(BeanUtils.toBean(store, StoreRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得门店分页")
    @PreAuthorize("@ss.hasPermission('store:store:query')")
    public CommonResult<PageResult<StoreRespVO>> getStorePage(@Valid StorePageReqVO pageReqVO) {
        PageResult<StoreDO> pageResult = storeService.getStorePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StoreRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出门店 Excel")
    @PreAuthorize("@ss.hasPermission('store:store:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStoreExcel(@Valid StorePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StoreDO> list = storeService.getStorePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "门店.xls", "数据", StoreRespVO.class,
                        BeanUtils.toBean(list, StoreRespVO.class));
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得门店导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<StoreImportExcelVO> list = Collections.singletonList(
                StoreImportExcelVO.builder().storeCode("S001").storeName("示例门店")
                        .platform("美团").city("杭州").manager("张三")
                        .phone("13800138000").status("1").build()
        );
        // 输出
        ExcelUtils.write(response, "门店导入模板.xls", "门店列表", StoreImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入门店")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('store:store:import')")
    public CommonResult<StoreImportRespVO> importExcel(@RequestParam("file") MultipartFile file,
                                                       @RequestParam(value = "updateSupport", required = false,
                                                               defaultValue = "false") Boolean updateSupport) throws Exception {
        List<StoreImportExcelVO> list = ExcelUtils.read(file, StoreImportExcelVO.class);
        return success(storeService.importStoreList(list, updateSupport));
    }

}
