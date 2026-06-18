package com.smarthmalik.metalpulse.core.ai;

import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.service.TradeService;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

// Structured per-user data (balance, trades) is fetched live here, never embedded into
// the vector store. Tools always read the user from ToolContext — never an LLM-supplied
// id — so they can only ever act on the authenticated caller's own data.
@Component
@RequiredArgsConstructor
public class PortfolioTools {

    private final TradeService tradeService;

    @Tool(description = "Get the authenticated user's current paper-trading account balance in USD")
    public BalanceDto currentBalance(ToolContext toolContext) {
        return tradeService.getBalance(contextUser(toolContext));
    }

    @Tool(description = "Get the authenticated user's most recent trades, newest first")
    public List<TradeDto> recentTrades(ToolContext toolContext) {
        return tradeService.getTradeHistory(contextUser(toolContext), 0, 10);
    }

    private User contextUser(ToolContext toolContext) {
        return (User) toolContext.getContext().get("user");
    }
}
