package com.glo.lending.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * Enables R2DBC auditing support for {@code @CreatedDate} and {@code @LastModifiedDate}.
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {
}

