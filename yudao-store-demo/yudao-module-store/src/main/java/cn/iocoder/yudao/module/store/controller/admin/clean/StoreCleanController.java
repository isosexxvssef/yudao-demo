package cn.iocoder.yudao.module.store.controller.admin.clean;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.store.controller.admin.clean.vo.StoreCleanLogPageReqVO;
import cn.iocoder.yudao.module.store.controller.admin.clean.vo.StoreCleanLogRespVO;
import cn.iocoder.yudao.module.store.dal.dataobject.clean.StoreCleanLogDO;
import cn.iocoder.yudao.module.store.service.clean.StoreCleanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 门店数据清洗")
@RestController
@RequestMapping("/store/clean")
@Validated
public class StoreCleanController {

    @Resource
    private StoreCleanService storeCleanService;

    @PostMapping("/trigger")
    @Operation(summary = "手动触发门店数据清洗")
    @Parameter(name = "triggerType", description = "触发方式（2=手动触发）", example = "2")
    @PreAuthorize("@ss.hasPermission('store:clean:trigger')")
    public CommonResult<StoreCleanLogRespVO> triggerClean(
            @RequestParam(value = "triggerType", required = false, defaultValue = "2") Integer triggerType) {
        StoreCleanLogDO cleanLog = storeCleanService.clean(triggerType);
        return success(BeanUtils.toBean(cleanLog, StoreCleanLogRespVO.class));
    }

    @GetMapping("/log/page")
    @Operation(summary = "获得清洗日志分页")
    @PreAuthorize("@ss.hasPermission('store:clean:query')")
    public CommonResult<PageResult<StoreCleanLogRespVO>> getCleanLogPage(@Valid StoreCleanLogPageReqVO pageReqVO) {
        PageResult<StoreCleanLogDO> pageResult = storeCleanService.getCleanLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StoreCleanLogRespVO.class));
    }

}
