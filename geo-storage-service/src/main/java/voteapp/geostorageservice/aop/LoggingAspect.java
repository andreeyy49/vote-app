package voteapp.geostorageservice.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private long startTime;

    private String methodName;

    private String className;

    private LoggingLevel level;

    private final HttpServletRequest request;

    private String beforeLog = ">> method:{}({}) uri:{} class:{}";

    private String afterLog = "<< method:{}({}):{}ms uri:{} class:{}";

    LoggingAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before(value = "@within(logging)", argNames = "joinPoint,logging")
    public void log(JoinPoint joinPoint, Logging logging) {
        Object[] args = joinPoint.getArgs();
        List<Object> argList = initData(args, joinPoint, logging);

        printLog(argList, true);

        startTime = System.currentTimeMillis();
    }

    @After(value = "@within(logging)", argNames = "joinPoint,logging")
    public void logReturn(JoinPoint joinPoint, Logging logging) {
        Object[] args = joinPoint.getArgs();
        List<Object> argList = initData(args, joinPoint, logging);

        printLog(argList, false);
    }

    private List<Object> initData(Object[] args, JoinPoint joinPoint, Logging logging) {
        level = logging.level();
        List<Object> argList = Arrays.asList(args);
        argList = argList.stream().map(el -> {
            if (el.getClass().equals(String.class) && ((String) el).length() > 60) {
                el = "image";
            }
            return el;
        }).toList();
        className = joinPoint.getTarget().getClass().getName();
        className = className.replace("social.network.socialnetworkgeo.", "");
        methodName = joinPoint.getSignature().getName();

        return argList;
    }

    private void printLog(List<Object> argList, boolean isBefore) {
        switch (level) {
            case ERROR: {
                if (isBefore) {
                    log.error(beforeLog, methodName, argList, request.getRequestURI(), className);
                } else {
                    log.error(afterLog, methodName, argList, System.currentTimeMillis() - startTime, request.getRequestURI(), className);
                }
                break;
            }

            case INFO: {
                if (isBefore) {
                    log.info(beforeLog, methodName, argList, request.getRequestURI(), className);
                } else {
                    log.info(afterLog, methodName, argList, System.currentTimeMillis() - startTime, request.getRequestURI(), className);
                }
                break;
            }

            case DEBUG: {
                if (isBefore) {
                    log.debug(beforeLog, methodName, argList, request.getRequestURI(), className);
                } else {
                    log.debug(afterLog, methodName, argList, System.currentTimeMillis() - startTime, request.getRequestURI(), className);
                }
                break;
            }

            case WARNING:
                if (isBefore) {
                    log.warn(beforeLog, methodName, argList, request.getRequestURI(), className);
                } else {
                    log.warn(afterLog, methodName, argList, System.currentTimeMillis() - startTime, request.getRequestURI(), className);
                }
                break;
        }
    }
}
