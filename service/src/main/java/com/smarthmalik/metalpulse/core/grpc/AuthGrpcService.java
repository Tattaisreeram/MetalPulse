package com.smarthmalik.metalpulse.core.grpc;

import com.smarthmalik.metalpulse.dto.request.LoginRequest;
import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.AuthResponse;
import com.smarthmalik.metalpulse.core.facade.AuthFacade;
import com.smarthmalik.metalpulse.grpc.v1.AuthServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthFacade authFacade;

    @Override
    public void register(
            com.smarthmalik.metalpulse.grpc.v1.RegisterRequest request,
            StreamObserver<com.smarthmalik.metalpulse.grpc.v1.AuthResponse> responseObserver) {

        try {
            RegisterRequest dto = new RegisterRequest(
                    request.getUsername(), request.getEmail(), request.getPassword());
            AuthResponse result = authFacade.register(dto).data();
            responseObserver.onNext(toProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC register failed", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void login(
            com.smarthmalik.metalpulse.grpc.v1.LoginRequest request,
            StreamObserver<com.smarthmalik.metalpulse.grpc.v1.AuthResponse> responseObserver) {

        try {
            LoginRequest dto = new LoginRequest(request.getUsername(), request.getPassword());
            AuthResponse result = authFacade.login(dto).data();
            responseObserver.onNext(toProto(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC login failed", e);
            responseObserver.onError(io.grpc.Status.UNAUTHENTICATED
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private com.smarthmalik.metalpulse.grpc.v1.AuthResponse toProto(AuthResponse r) {
        return com.smarthmalik.metalpulse.grpc.v1.AuthResponse.newBuilder()
                .setToken(r.token())
                .setTokenType(r.tokenType())
                .setUserId(r.userId())
                .setUsername(r.username())
                .setEmail(r.email())
                .setRole(r.role())
                .build();
    }
}
