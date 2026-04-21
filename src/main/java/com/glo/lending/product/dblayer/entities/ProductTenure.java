package com.glo.lending.product.dblayer.entities;

import com.glo.lending.product.model.enums.TenureType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table("product_tenures")
public class ProductTenure {

    @Id
    private UUID id;
    @Column("product_id")
    private UUID productId;
    @Column("tenure_value")
    private Integer tenureValue;
    @Column("tenure_type")
    private TenureType tenureType;
    @Column("is_fixed")
    private Boolean isFixed;


}

