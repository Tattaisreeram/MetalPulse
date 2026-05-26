package com.smarthmalik.metalpulse.core.grpc;

import com.smarthmalik.metalpulse.core.dao.UserDAO;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.security.JwtTokenProvider;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@Slf4j
@GrpcGlobalServerInterceptor
@RequiredArgsConstructor
public class GrpcAuthInterceptor implements ServerInterceptor {

    public static final Context.Key<User> USER_KEY = Context.key("authenticated-user");

    private static final Metadata.Key<String> AUTH_HEADER =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDAO;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtTokenProvider.extractUsername(token);
                User user = userDAO.findByUsername(username).orElse(null);
                if (user != null) {
                    Context ctx = Context.current().withValue(USER_KEY, user);
                    return Contexts.interceptCall(ctx, call, headers, next);
                }
            } catch (Exception e) {
                log.debug("Invalid JWT in gRPC call: {}", e.getMessage());
            }
        }

        // No valid token — Auth RPCs (Register/Login) proceed without a user in context
        return next.startCall(call, headers);
    }
}
