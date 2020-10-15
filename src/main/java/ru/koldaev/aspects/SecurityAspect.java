package ru.koldaev.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import javax.servlet.http.HttpSession;

@Aspect
public class SecurityAspect {

    @Around("execution(* ru.koldaev.controllers.*.*(..))")
    public Object security(ProceedingJoinPoint joinPoint) throws Throwable
    {
        if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof HttpSession)
        {
            if (((HttpSession) joinPoint.getArgs()[0]).getAttribute("currentUser") == null)
                return "/errorPage";
        }
        return joinPoint.proceed();
    }
}
