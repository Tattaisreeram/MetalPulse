package com.smarthmalik.metalpulse.core.controller;

import com.smarthmalik.metalpulse.dto.request.DepositWithdrawRequest;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import com.smarthmalik.metalpulse.core.constant.URLConstants;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.facade.TradeFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trading", description = "Buy, sell, hold metals and manage your balance")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class TradeController {

    private final TradeFacade tradeFacade;

    @Operation(summary = "Buy a precious metal at the current spot price")
    @PostMapping(URLConstants.TRADE_BUY)
    public ApiResponse<TradeDto> buy(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeRequest request) {
        return tradeFacade.buy(user, request);
    }

    @Operation(summary = "Sell a precious metal at the current spot price")
    @PostMapping(URLConstants.TRADE_SELL)
    public ApiResponse<TradeDto> sell(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeRequest request) {
        return tradeFacade.sell(user, request);
    }

    @Operation(summary = "Record a hold position without executing a trade")
    @PostMapping(URLConstants.TRADE_HOLD)
    public ApiResponse<TradeDto> hold(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeRequest request) {
        return tradeFacade.hold(user, request);
    }

    @Operation(summary = "Deposit funds into your account")
    @PostMapping(URLConstants.TRADE_DEPOSIT)
    public ApiResponse<BalanceDto> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return tradeFacade.deposit(user, request);
    }

    @Operation(summary = "Withdraw funds from your account")
    @PostMapping(URLConstants.TRADE_WITHDRAW)
    public ApiResponse<BalanceDto> withdraw(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return tradeFacade.withdraw(user, request);
    }

    @Operation(summary = "Get your current account balance")
    @GetMapping(URLConstants.TRADE_BALANCE)
    public ApiResponse<BalanceDto> getBalance(@AuthenticationPrincipal User user) {
        return tradeFacade.getBalance(user);
    }

    @Operation(summary = "Get your paginated trade history")
    @GetMapping(URLConstants.TRADE_HISTORY)
    public ApiResponse<List<TradeDto>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return tradeFacade.getTradeHistory(user, page, size);
    }
}
