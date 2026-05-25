package com.smarthmalik.metalpulse.core.dao.impl;

import com.smarthmalik.metalpulse.core.dao.TradeDAO;
import com.smarthmalik.metalpulse.core.entity.Trade;
import com.smarthmalik.metalpulse.core.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TradeDAOImpl implements TradeDAO {

    private final TradeRepository tradeRepository;

    @Override
    public Trade save(Trade trade) {
        return tradeRepository.save(trade);
    }

    @Override
    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    @Override
    public List<Trade> findByUserId(Long userId) {
        return tradeRepository.findByUserId(userId);
    }

    @Override
    public Page<Trade> findByUserId(Long userId, Pageable pageable) {
        return tradeRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public List<Trade> findByUserIdAndTradeType(Long userId, Trade.TradeType tradeType) {
        return tradeRepository.findByUserIdAndTradeType(userId, tradeType);
    }

    @Override
    public List<Trade> findByUserIdAndMetal(Long userId, String metal) {
        return tradeRepository.findByUserIdAndMetal(userId, metal);
    }
}
