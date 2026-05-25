package com.smarthmalik.metalpulse.core.dao;

import com.smarthmalik.metalpulse.core.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TradeDAO {

    Trade save(Trade trade);

    Optional<Trade> findById(Long id);

    List<Trade> findByUserId(Long userId);

    Page<Trade> findByUserId(Long userId, Pageable pageable);

    List<Trade> findByUserIdAndTradeType(Long userId, Trade.TradeType tradeType);

    List<Trade> findByUserIdAndMetal(Long userId, String metal);
}
