package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.dto.request.DepositWithdrawRequest;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import com.smarthmalik.metalpulse.core.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface TradeService {

    TradeDto buy(User user, TradeRequest request);

    TradeDto sell(User user, TradeRequest request);

    TradeDto hold(User user, TradeRequest request);

    BalanceDto deposit(User user, DepositWithdrawRequest request);

    BalanceDto withdraw(User user, DepositWithdrawRequest request);

    BalanceDto getBalance(User user);

    List<TradeDto> getTradeHistory(User user, int page, int size);

    void createBonusTrade(Long userId, BigDecimal amount);
}
