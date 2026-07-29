package cn.iocoder.yudao.module.store.dal.dataobject.store;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;

/**
 * 门店 DO
 *
 * @author 芋道源码
 */
@TableName("zy_store")
@KeySequence("zy_store_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 门店编码，租户内唯一
     */
    private String storeCode;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 平台
     */
    private String platform;
    /**
     * 城市
     */
    private String city;
    /**
     * 负责人
     */
    private String manager;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 营业状态（营业/停业）
     *
     * 字典 store_status
     */
    private String status;


}
