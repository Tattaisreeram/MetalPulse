package com.smarthmalik.metalpulse.core.facade;

import com.smarthmalik.metalpulse.dto.request.DepositWithdrawRequest;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import com.smarthmalik.metalpulse.event.TradeExecutedEvent;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeFacade {

    private final TradeService tradeService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ApiResponse<TradeDto> buy(User user, TradeRequest request) {
        TradeDto trade = tradeService.buy(user, request);
        eventPublisher.publishEvent(tradeEvent(user, trade));
        return ApiResponse.success("Purchase successful", trade);
    }

    @Transactional
    public ApiResponse<TradeDto> sell(User user, TradeRequest request) {
        TradeDto trade = tradeService.sell(user, request);
        eventPublisher.publishEvent(tradeEvent(user, trade));
        return ApiResponse.success("Sale successful", trade);
    }

    @Transactional
    public ApiResponse<TradeDto> hold(User user, TradeRequest request) {
        TradeDto trade = tradeService.hold(user, request);
        eventPublisher.publishEvent(tradeEvent(user, trade));
        return ApiResponse.success("Hold position recorded", trade);
    }

    private static TradeExecutedEvent tradeEvent(User user, TradeDto trade) {
        return new TradeExecutedEvent(
                trade.id(), user.getId(), user.getUsername(),
                trade.tradeType(), trade.metal(), trade.currency(),
                trade.weightUnit(), trade.quantity(), trade.totalAmount(),
                trade.createdAt());
    }

    @Transactional
    public ApiResponse<BalanceDto> deposit(User user, DepositWithdrawRequest request) {
        BalanceDto balance = tradeService.deposit(user, request);
        return ApiResponse.success("Deposit successful", balance);
    }

    @Transactional
    public ApiResponse<BalanceDto> withdraw(User user, DepositWithdrawRequest request) {
        BalanceDto balance = tradeService.withdraw(user, request);
        return ApiResponse.success("Withdrawal successful", balance);
    }

    @Transactional(readOnly = true)
    public ApiResponse<BalanceDto> getBalance(User user) {
        return ApiResponse.success(tradeService.getBalance(user));
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<TradeDto>> getTradeHistory(User user, int page, int size) {
        List<TradeDto> trades = tradeService.getTradeHistory(user, page, size);
        return ApiResponse.success(trades);
    }
}
