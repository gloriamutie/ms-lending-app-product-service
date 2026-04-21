package com.glo.lending.product.dblayer.entities;

import com.glo.lending.product.model.enums.CalculationType;
import com.glo.lending.product.model.enums.FeeType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Table("product_fees")
public class ProductFee {

    @Id
    private UUID id;
    @Column("product_id")
    private UUID productId;
    @Column("fee_type")
    private FeeType feeType;
    @Column("calculation_type")
    private CalculationType calculationType;
    @Column("amount")
    private BigDecimal amount;
    @Column("description")
    private String description;
    @Column("trigger_days_after_due")
    private Integer triggerDaysAfterDue;
    @Column("apply_at_origination")
    private Boolean applyAtOrigination;
}

