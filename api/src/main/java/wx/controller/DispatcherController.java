package wx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import wx.rarf.resource.Resource;
import wx.rarf.resource.bag.ResourceBag;
import wx.rarf.handler.AbstractHandler;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.throwable.RARFThrowable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * Created by apple on 15/11/13.
 */
//@Controller
public class DispatcherController {

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private ApplicationContext appContext;

//    @RequestMapping(value = {"/user/{user_id}", "/user/{user_id}/*", "/user/{user_id}/*/*", "/user/{user_id}/*/*/*", "/user/{user_id}/*/*/*/*", "/user/{user_id}/*/*/*/*/*"})
    @ResponseBody
    public DeferredResult<String> dispather_user(
            @PathVariable String user_id,//路徑參數,在统一的鉴权的Aspect中完成替换
            HttpServletResponse response//固定写法，把HttpServletResponse放在最后
    ) {

        //獲取請求的Action
        String Action = (String) httpServletRequest.getAttribute("action");

        //內部調試容錯，為了防止Action為空
        if (Action == null) {
            Action = httpServletRequest.getParameter("action") != null ? httpServletRequest.getParameter("action") : httpServletRequest.getMethod();
        }

        //创建延迟返回句柄
        final DeferredResult<String> deferredResult = new DeferredResult<String>(90000l);

        //获取Uri的资源映射
        List<Object[]> uriMapping = Resource.uriResolver(httpServletRequest.getRequestURI());

        //如果有requestData则获取requestData
        Optional<String> requestData = Optional.ofNullable(String.valueOf(httpServletRequest.getParameter("requestData") != null ? httpServletRequest.getParameter("requestData") : httpServletRequest.getAttribute("requestData")));

        //初始化資源包
        ResourceBag resourceBag = new ResourceBag.Builder(ResourceBag.ActionResolver(Action), httpServletRequest.getRequestURI())
                .ofDeferredResult(deferredResult)
                .ofUserId(user_id)
                .ofRequestData(requestData)
                .build();

        //获取第一个资源包
        if (uriMapping.size() > 0) {

            //判断第一个资源包是分发给资源处理器还是逻辑处理器,鉴于目前是把user当做了一级资源
            if (("user".equals(uriMapping.get(0)[0]) && "logic".equals(uriMapping.get(1)[0]))
                    || "logic".equals(uriMapping.get(0)[0])) {

                String logicHandlerName = "";

                //如果第一个为Logic
                if ("user".equals(uriMapping.get(0)[0])) {
                    logicHandlerName = ((Optional<String>) uriMapping.get(1)[1]).get();
                } else {
                    logicHandlerName = ((Optional<String>) uriMapping.get(0)[1]).get();
                }

//                //获取处理器
//                LogicHandler logicHandler = (LogicHandler) appContext.getBean(logicHandlerName + "LogicHandler");
//
//                //发送资源包粗去
//                logicHandler.handle(resourceBag).subscribe((r) -> {
//
//                            if (!deferredResult.hasResult()) {
//                                //资源流动结束，返回到这里
//                                deferredResult.setResult(new RARFThrowable.Builder().code(0).desc("Success").build().toJsonString());
//                            }
//                        },
//                        (throwable) -> {
//
//                            if (!deferredResult.hasResult()) {
//                                deferredResult.setResult(new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("Error,输入的路径有误或发生内部错误!").build().toJsonString());
//                            }
//
//                        });

                return deferredResult;

            }

            //如果發現第一個資源包,根据资源包获取处理器
            AbstractHandler resourceHandler = (AbstractHandler) appContext.getBean(String.valueOf((uriMapping.get(0))[0]) + "ResourceHandler");

            resourceHandler.handle(resourceBag).subscribe((r) -> {

                        if (!deferredResult.hasResult()) {
                            //资源流动结束，返回到这里
                            deferredResult.setResult(new RARFThrowable.Builder().code(0).desc("Success").build().toJsonString());
                        }
                    },
                    (throwable) -> {

                        if (!deferredResult.hasResult()) {
                            deferredResult.setResult(new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("Error,输入的路径有误或发生内部错误!").build().toJsonString());
                        }

                    });

        } else {

            deferredResult.setResult(new RARFThrowable.Builder().desc("No Resource Query!").build().toJsonString());

        }

        return deferredResult;
    }


    /**
     * @return
     * @function
     */
    public Observable<ResourceBag> forward(ResourceBag resourceBag) {


        //鑒於是流動到下一個資源，則將流下標加一
        resourceBag.increaseFlowIndex();

        //獲取新的資源處理句柄
        AbstractHandler resourceHandler = (AbstractHandler) appContext.getBean(String.valueOf((resourceBag.getUriMapping().get(resourceBag.getFlowIndex()))[0]) + "ResourceHandler");

        //注意，如果這邊為空是會報錯的
        return resourceHandler.handle(resourceBag);

    }


    /**
     * @region Inner Help
     */


    public static void main(String args[]) {

        DispatcherController dispatcherController = new DispatcherController();

        //0,1,2,3,4,5
        Resource.uriResolver("http://localhost:10086/user/qnZ5awrOszYd1iFb3iLF9DJN2kCJ2B02FgzmX3a2F8gVM83D/aarf/1/resource1?requestData={}");

    }
}
