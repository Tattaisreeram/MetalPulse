package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.dto.request.DepositWithdrawRequest;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import com.smarthmalik.metalpulse.exception.InsufficientBalanceException;
import com.smarthmalik.metalpulse.core.dao.TradeDAO;
import com.smarthmalik.metalpulse.core.dao.UserDAO;
import com.smarthmalik.metalpulse.core.entity.Trade;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.helper.FxRateHelper;
import com.smarthmalik.metalpulse.core.helper.MetalPriceHelper;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeDAO tradeDAO;
    private final UserDAO userDAO;
    private final MetalPriceHelper metalPriceHelper;
    private final FxRateHelper fxRateHelper;
    private final EntityDTOMapper mapper;

    @Override
    public TradeDto buy(User user, TradeRequest request) {
        GoldbrokerSpotPriceResponse spotPrice =
                metalPriceHelper.fetchSpotPrice(request.metal(), request.currency(), request.weightUnit());

        BigDecimal pricePerUnit = spotPrice.price();
        BigDecimal totalCost    = pricePerUnit.multiply(request.quantity()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalCostUsd = fxRateHelper.toUsd(totalCost, request.currency());

        if (user.getBalance().compareTo(totalCostUsd) < 0) {
            throw new InsufficientBalanceException(totalCostUsd, user.getBalance());
        }

        user.setBalance(user.getBalance().subtract(totalCostUsd));
        userDAO.save(user);

        Trade trade = Trade.builder()
                .user(user)
                .tradeType(Trade.TradeType.BUY)
                .metal(request.metal())
                .currency(request.currency())
                .weightUnit(request.weightUnit())
                .quantity(request.quantity())
                .pricePerUnit(pricePerUnit)
                .totalAmount(totalCost)
                .status(Trade.TradeStatus.COMPLETED)
                .build();

        Trade saved = tradeDAO.save(trade);
        log.info("BUY trade completed: userId={} metal={} qty={} total={}",
                user.getId(), request.metal(), request.quantity(), totalCost);
        return mapper.toTradeDto(saved);
    }

    @Override
    public TradeDto sell(User user, TradeRequest request) {
        GoldbrokerSpotPriceResponse spotPrice =
                metalPriceHelper.fetchSpotPrice(request.metal(), request.currency(), request.weightUnit());

        BigDecimal pricePerUnit = spotPrice.price();
        BigDecimal saleValue    = pricePerUnit.multiply(request.quantity()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal saleValueUsd = fxRateHelper.toUsd(saleValue, request.currency());

        user.setBalance(user.getBalance().add(saleValueUsd));
        userDAO.save(user);

        Trade trade = Trade.builder()
                .user(user)
                .tradeType(Trade.TradeType.SELL)
                .metal(request.metal())
                .currency(request.currency())
                .weightUnit(request.weightUnit())
                .quantity(request.quantity())
                .pricePerUnit(pricePerUnit)
                .totalAmount(saleValue)
                .status(Trade.TradeStatus.COMPLETED)
                .build();

        Trade saved = tradeDAO.save(trade);
        log.info("SELL trade completed: userId={} metal={} qty={} total={}",
                user.getId(), request.metal(), request.quantity(), saleValue);
        return mapper.toTradeDto(saved);
    }

    @Override
    public TradeDto hold(User user, TradeRequest request) {
        GoldbrokerSpotPriceResponse spotPrice =
                metalPriceHelper.fetchSpotPrice(request.metal(), request.currency(), request.weightUnit());

        BigDecimal pricePerUnit = spotPrice.price();
        BigDecimal holdValue    = pricePerUnit.multiply(request.quantity()).setScale(4, RoundingMode.HALF_UP);

        Trade trade = Trade.builder()
                .user(user)
                .tradeType(Trade.TradeType.HOLD)
                .metal(request.metal())
                .currency(request.currency())
                .weightUnit(request.weightUnit())
                .quantity(request.quantity())
                .pricePerUnit(pricePerUnit)
                .totalAmount(holdValue)
                .status(Trade.TradeStatus.COMPLETED)
                .build();

        Trade saved = tradeDAO.save(trade);
        log.info("HOLD recorded: userId={} metal={} qty={}", user.getId(), request.metal(), request.quantity());
        return mapper.toTradeDto(saved);
    }

    @Override
    public BalanceDto deposit(User user, DepositWithdrawRequest request) {
        BigDecimal usdAmount = fxRateHelper.toUsd(request.amount(), request.currency());
        user.setBalance(user.getBalance().add(usdAmount));
        userDAO.save(user);

        Trade trade = Trade.builder()
                .user(user)
                .tradeType(Trade.TradeType.DEPOSIT)
                .currency(request.currency())
                .totalAmount(request.amount())
                .status(Trade.TradeStatus.COMPLETED)
                .build();
        tradeDAO.save(trade);

        log.info("DEPOSIT: userId={} amount={} {} = {} USD", user.getId(), request.amount(), request.currency(), usdAmount);
        return mapper.toBalanceDto(user);
    }

    @Override
    public BalanceDto withdraw(User user, DepositWithdrawRequest request) {
        BigDecimal usdAmount = fxRateHelper.toUsd(request.amount(), request.currency());

        if (user.getBalance().compareTo(usdAmount) < 0) {
            throw new InsufficientBalanceException(usdAmount, user.getBalance());
        }

        user.setBalance(user.getBalance().subtract(usdAmount));
        userDAO.save(user);

        Trade trade = Trade.builder()
                .user(user)
                .tradeType(Trade.TradeType.WITHDRAW)
                .currency(request.currency())
                .totalAmount(request.amount())
                .status(Trade.TradeStatus.COMPLETED)
                .build();
        tradeDAO.save(trade);

        log.info("WITHDRAW: userId={} amount={} {} = {} USD", user.getId(), request.amount(), request.currency(), usdAmount);
        return mapper.toBalanceDto(user);
    }

    @Override
    public BalanceDto getBalance(User user) {
        return mapper.toBalanceDto(user);
    }

    @Override
    public List<TradeDto> getTradeHistory(User user, int page, int size) {
        Page<Trade> tradePage = tradeDAO.findByUserId(user.getId(), PageRequest.of(page, size));
        return tradePage.getContent().stream().map(mapper::toTradeDto).toList();
    }

    @Override
    // Called by AuthFacade in the same transaction that just created the user, so getReferenceById should
    // never resolve to a missing row. If it does, it indicates transaction propagation failure or external
    // DB modification mid-transaction.
    public void createBonusTrade(Long userId, BigDecimal amount) {
        User userRef = userDAO.getReferenceById(userId);
        tradeDAO.save(Trade.builder()
                .user(userRef)
                .tradeType(Trade.TradeType.BONUS)
                .currency("USD")
                .totalAmount(amount)
                .status(Trade.TradeStatus.COMPLETED)
                .build());
    }
}
