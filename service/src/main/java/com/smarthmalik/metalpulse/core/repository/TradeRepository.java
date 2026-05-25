package com.smarthmalik.metalpulse.core.repository;

import com.smarthmalik.metalpulse.core.constant.SQLQueryConstants;
import com.smarthmalik.metalpulse.core.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query(SQLQueryConstants.TRADE_FIND_BY_USER_ID)
    List<Trade> findByUserId(@Param("userId") Long userId);

    Page<Trade> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(SQLQueryConstants.TRADE_FIND_BY_USER_AND_TYPE)
    List<Trade> findByUserIdAndTradeType(
            @Param("userId") Long userId,
            @Param("tradeType") Trade.TradeType tradeType);

    @Query(SQLQueryConstants.TRADE_FIND_BY_USER_AND_METAL)
    List<Trade> findByUserIdAndMetal(
            @Param("userId") Long userId,
            @Param("metal") String metal);
}
