package com.landriskai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final LandRiskAiProperties props;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public RateLimitFilter(LandRiskAiProperties props) {
        this.props = props;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        int maxRequests = props.getSecurity().getMaxRequestsPerMinute();
        if (maxRequests <= 0) {
            return true;
        }
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        int maxRequests = props.getSecurity().getMaxRequestsPerMinute();
        if (maxRequests <= 0) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = resolveClientKey(request);
        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter());

        if (!counter.tryIncrement(maxRequests)) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(counter.secondsUntilReset()));
            response.getWriter().write("Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static final class WindowCounter {
        private volatile long windowStart = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);

        boolean tryIncrement(int maxRequests) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= WINDOW.toMillis()) {
                windowStart = now;
                count.set(0);
            }
            return count.incrementAndGet() <= maxRequests;
        }

        long secondsUntilReset() {
            long elapsed = System.currentTimeMillis() - windowStart;
            long remainingMs = Math.max(0, WINDOW.toMillis() - elapsed);
            return Math.max(1, remainingMs / 1000);
        }
    }
}
