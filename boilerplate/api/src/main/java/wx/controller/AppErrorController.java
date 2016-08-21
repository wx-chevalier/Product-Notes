package wx.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import wx.application.utils.JSONResponse;
import wx.application.utils.StatusCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by apple on 16/6/13.
 */
@RestController
public class AppErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Value("${debug}")
    private boolean debug;

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    JSONResponse error(HttpServletRequest request, HttpServletResponse response) {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring.
        // Here we just define response body.
        String error = "处理错误!";

        //判断错误描述
        if (String.valueOf(response.getStatus()).equals(StatusCode.REQUEST_ERROR_PARAMS)) {
            error = "请求参数有误,请检测必填参数类型";
        }


        return JSONResponse.builder()
                .code(String.valueOf(response.getStatus()))
                .error("error")
                .trace(this.getErrorAttributes(request, debug))
                .build();
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private String getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {

        StringBuilder error = new StringBuilder();

        RequestAttributes requestAttributes = new ServletRequestAttributes(request);

        errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace).forEach((s, o) -> {
            error.append(s + ":" + o.toString() + "\n");
        });
        
        return error.toString();
    }
}
