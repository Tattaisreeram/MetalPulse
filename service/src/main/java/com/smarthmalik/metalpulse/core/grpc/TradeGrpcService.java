package com.smarthmalik.metalpulse.core.grpc;

import com.google.protobuf.Timestamp;
import com.smarthmalik.metalpulse.dto.request.DepositWithdrawRequest;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.BalanceDto;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.facade.TradeFacade;
import com.smarthmalik.metalpulse.grpc.v1.BalanceRequest;
import com.smarthmalik.metalpulse.grpc.v1.BalanceResponse;
import com.smarthmalik.metalpulse.grpc.v1.GetBalanceRequest;
import com.smarthmalik.metalpulse.grpc.v1.HistoryRequest;
import com.smarthmalik.metalpulse.grpc.v1.HistoryResponse;
import com.smarthmalik.metalpulse.grpc.v1.TradeResponse;
import com.smarthmalik.metalpulse.grpc.v1.TradeServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TradeGrpcService extends TradeServiceGrpc.TradeServiceImplBase {

    private final TradeFacade tradeFacade;

    @Override
    public void buy(com.smarthmalik.metalpulse.grpc.v1.TradeRequest request,
                    StreamObserver<TradeResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            TradeDto result = tradeFacade.buy(user, toTradeRequest(request)).data();
            responseObserver.onNext(toTradeProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC buy failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void sell(com.smarthmalik.metalpulse.grpc.v1.TradeRequest request,
                     StreamObserver<TradeResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            TradeDto result = tradeFacade.sell(user, toTradeRequest(request)).data();
            responseObserver.onNext(toTradeProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC sell failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void hold(com.smarthmalik.metalpulse.grpc.v1.TradeRequest request,
                     StreamObserver<TradeResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            TradeDto result = tradeFacade.hold(user, toTradeRequest(request)).data();
            responseObserver.onNext(toTradeProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC hold failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deposit(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            BalanceDto result = tradeFacade.deposit(user, toBalanceRequest(request)).data();
            responseObserver.onNext(toBalanceProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC deposit failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void withdraw(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            BalanceDto result = tradeFacade.withdraw(user, toBalanceRequest(request)).data();
            responseObserver.onNext(toBalanceProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC withdraw failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getBalance(GetBalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            BalanceDto result = tradeFacade.getBalance(user).data();
            responseObserver.onNext(toBalanceProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getBalance failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getTradeHistory(HistoryRequest request, StreamObserver<HistoryResponse> responseObserver) {
        User user = GrpcAuthInterceptor.USER_KEY.get();
        if (user == null) { rejectUnauthenticated(responseObserver); return; }
        try {
            List<TradeDto> trades = tradeFacade.getTradeHistory(user, request.getPage(), request.getSize()).data();
            HistoryResponse.Builder builder = HistoryResponse.newBuilder();
            if (trades != null) trades.forEach(t -> builder.addTrades(toTradeProto(t)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getTradeHistory failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // ── mapping helpers ────────────────────────────────────────────────────────

    private TradeRequest toTradeRequest(com.smarthmalik.metalpulse.grpc.v1.TradeRequest r) {
        return new TradeRequest(
                r.getMetal(),
                r.getCurrency(),
                r.getWeightUnit(),
                BigDecimal.valueOf(r.getQuantity())
        );
    }

    private DepositWithdrawRequest toBalanceRequest(BalanceRequest r) {
        return new DepositWithdrawRequest(BigDecimal.valueOf(r.getAmount()), r.getCurrency());
    }

    private TradeResponse toTradeProto(TradeDto t) {
        TradeResponse.Builder b = TradeResponse.newBuilder()
                .setId(t.id() != null ? t.id() : 0)
                .setTradeType(t.tradeType() != null ? t.tradeType() : "")
                .setMetal(t.metal() != null ? t.metal() : "")
                .setMetalName(t.metalName() != null ? t.metalName() : "")
                .setCurrency(t.currency() != null ? t.currency() : "")
                .setWeightUnit(t.weightUnit() != null ? t.weightUnit() : "")
                .setQuantity(t.quantity() != null ? t.quantity().doubleValue() : 0)
                .setPricePerUnit(t.pricePerUnit() != null ? t.pricePerUnit().doubleValue() : 0)
                .setTotalAmount(t.totalAmount() != null ? t.totalAmount().doubleValue() : 0)
                .setStatus(t.status() != null ? t.status() : "");

        if (t.createdAt() != null) {
            b.setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(t.createdAt().toEpochSecond(ZoneOffset.UTC))
                    .setNanos(t.createdAt().getNano())
                    .build());
        }
        return b.build();
    }

    private BalanceResponse toBalanceProto(BalanceDto b) {
        return BalanceResponse.newBuilder()
                .setUserId(b.userId() != null ? b.userId() : 0)
                .setUsername(b.username() != null ? b.username() : "")
                .setBalance(b.balance() != null ? b.balance().doubleValue() : 0)
                .build();
    }

    private <T> void rejectUnauthenticated(StreamObserver<T> obs) {
        obs.onError(Status.UNAUTHENTICATED.withDescription("Valid Bearer token required").asRuntimeException());
    }
}
