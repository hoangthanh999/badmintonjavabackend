package com.hoangthanhhong.badminton.aspect;

import com.hoangthanhhong.badminton.security.SecurityUtils;
import com.hoangthanhhong.badminton.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;

    @Pointcut("@annotation(com.hoangthanhhong.badminton.annotation.Auditable)")
    public void auditableMethod() {
    }

    @AfterReturning(pointcut = "auditableMethod()", returning = "result")
    public void auditMethodExecution(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            Long userId = SecurityUtils.getCurrentUserId();

            auditService.log(
                    methodName.toUpperCase(),
                    className.toUpperCase(),
                    String.format("Executed %s.%s", className, methodName),
                    userId,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            log.error("Failed to audit method execution", e);
        }
    }
}
