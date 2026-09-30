package com.example.dzprovinceapi.shared.filter

import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter : OncePerRequestFilter() {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val ip = request.remoteAddr
        val bucket =
            buckets.computeIfAbsent(ip) {
                Bucket
                    .builder()
                    .addLimit {
                        it
                            .capacity(60)
                            .refillGreedy(60, Duration.ofMinutes(1))
                    }.build()
            }

        if (!bucket.tryConsume(1)) {
            response.status = 429
            response.setHeader("Retry-After", "60")
            return
        }

        filterChain.doFilter(request, response)
    }
}
