package wx.application.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by apple on 15/11/10.
 */

/**
 * @function 全局的鉴权切面
 */
//@Aspect
@Component("globalAuthAspect")
public class AuthAspect {

    final static Map<String, List<String>> publicAPIs = new HashMap<>();


    @Autowired
    private HttpServletRequest request;

    //默认日志句柄
    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger("com.lf.controller");


    //配置所有不需要鉴权的接口
    static {
        publicAPIs.put("UserLoginController", Arrays.asList(new String[]{
                "doLogin"
        }));
    }

    @Pointcut("bean(*Controller)")
    private void anyController() {
    }//定义一个切入点

    @Around("anyController()")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {

        return joinPoint.proceed();


//        Object[] args = joinPoint.getArgs();
//
//        if (args.length == 0 || !(args[0].getClass().getName().equals("java.lang.String"))) {
//            //目前只有第一個參數為String的才有可能是自動驗證
//            return joinPoint.proceed();
//
//        }
//
//        ////判断是否为免驗證
//        String methodClassName = joinPoint.getTarget().getClass().getName();
//        String methodName = joinPoint.getSignature().getName();
//
//        //判断是否为开放接口
//        if (publicAPIs.containsKey(methodClassName) && publicAPIs.get(methodClassName).contains(methodName)) {
//            return joinPoint.proceed();
//        }
//
//        //如果需要進行驗證的
//        String user_token = (String) args[0];
//
//
//        //获取返回对象
////        HttpServletResponse response = (HttpServletResponse) args[args.length - 1];
//
////        Optional<String> result = userTokenServiceImpl.queryUserIdByTokenAsync(user_token).toBlocking().first();
////
////        if (result.isPresent()) {
////
////            //鑒權成功，將user_id填入新參數
////            args[0] = result.get();
////            return joinPoint.proceed(args);
////
////        } else {
////
////            //如果用戶鑒權失敗
////            LOG.error("用户鉴权失败：" + user_token);
////            //記錄日誌
////            response.getWriter().print(new LFThrowable.Builder().code(ErrorConfig.CODE_1005).subCode(1).desc("用戶鉴权失败").build().toJsonString());
////            response.getWriter().close();
////        }
//
//
//        return joinPoint.proceed();

    }

}
