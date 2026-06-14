package com.example.courseprojectapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example.courseprojectapi.controller.*.*(..)) || " +
            "execution(* com.example.courseprojectapi.service.*.*(..))"
    )

    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();
        log.info("Đang thực hiện: {}", methodName);

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Thất bại: {}", methodName, e);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("{} thực hiện trong {} ms", methodName, executionTime);
        }
    }
}