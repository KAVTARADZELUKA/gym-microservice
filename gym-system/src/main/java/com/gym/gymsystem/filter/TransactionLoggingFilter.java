package com.gym.gymsystem.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = request.getHeader("X-Transaction-Id");
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }

        logger.info("Transaction Started: transactionId={}, method={}, URI={}",
                transactionId, request.getMethod(), request.getRequestURI());

        response.setHeader("X-Transaction-Id", transactionId);

        try {
            filterChain.doFilter(request, response);

            logger.info("Transaction Completed: transactionId={}, status={}, message={}",
                    transactionId, response.getStatus(), response.getStatus() == 200 ? "Success" : "Error");
        } catch (IOException ioEx) {
            logger.error("Transaction Failed: transactionId={}, I/O error={}", transactionId, ioEx.getMessage(), ioEx);
            throw ioEx;
        } catch (ServletException servletEx) {
            logger.error("Transaction Failed: transactionId={}, Servlet error={}", transactionId, servletEx.getMessage(), servletEx);
            throw servletEx;
        } catch (RuntimeException runtimeEx) {
            logger.error("Transaction Failed: transactionId={}, Runtime error={}", transactionId, runtimeEx.getMessage(), runtimeEx);
            throw runtimeEx;
        }
    }
}