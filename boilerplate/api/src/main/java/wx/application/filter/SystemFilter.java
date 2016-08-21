package wx.application.filter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.util.NestedServletException;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.HJSONObject;
import wx.rarf.utils.throwable.RARFThrowable;
import wx.rarf.utils.throwable.RequestException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;


/**
 * @author apple
 * @function 系统过滤器
 * @date: 2014-11-21
 */
@Component
public class SystemFilter implements Filter {
    // 日志

    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @function 执行过滤操作
     */
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        //
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "No-Cache");
        response.setHeader("Access-Control-Allow-Origin","*");
        //设置缓存时间，防止客户端多次重复请求
        response.setHeader("Cache-Control", "max-age=1");

        try {
//
//            //判斷用戶是否自帶了Action參數，如果麼有的話默認將Method添加到参数里
//            //添加对于Query方式与REST方式的双重支持
//
//            if (!request.getParameterMap().containsKey("action")) {
//                request.setAttribute("action", request.getMethod());
//            } else {
//                request.setAttribute("action", request.getParameter("action"));
//            }
//
//            //从requestBody中获取请求数据，放置到attribute中
//            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//
//            if (requestBody != null && requestBody.contains("=")) {
//
//                requestBody = requestBody.replace("&", "");
//
//                requestBody = requestBody.split("=").length == 2 ? requestBody.split("=")[1] : "";
//
//                //判断requestBody是否为JSONObject
//                if (request.getAttribute("requestData") == null && HJSONObject.getJsonType(requestBody) == 0) {
//
//                    JSONObject jsonObject = wx.rarf.utils.HJSONObject.parseObject(requestBody);
//
//                    if (jsonObject != null) {
//
//                        //判读请求体中是否含有action
//                        if (jsonObject.containsKey("action")) {
//                            request.setAttribute("action", jsonObject.get("action"));
//                        }
//
//                        request.setAttribute("requestData", jsonObject.toJSONString());
//
//                    }
//                }
//            }
//
//            if (request.getParameterMap().containsKey("requestData")) {
//
//
//                request.setAttribute("requestData", request.getParameterMap().get("requestData"));
//            }

            chain.doFilter(req, resp);


        } catch (Exception e) {

            e.printStackTrace();

            //判断是否为服务器内部错误
            if (e instanceof NestedServletException) {
                NestedServletException nServletException = (NestedServletException) e;

                if (nServletException.getCause() instanceof Exception) {
                    Exception exception = (Exception) nServletException.getCause();
                    if (exception instanceof RequestException) {
                        String desc = ((RequestException) exception).getDesc();
                        int code = ((RequestException) exception).getCode();
                        int subCode = ((RequestException) exception).getSubCode();
                        HJSONObject rtn = new HJSONObject();
                        rtn.put("code", code);
                        rtn.put("subCode", subCode);
                        rtn.put("desc", desc);
                        PrintWriter out = response.getWriter();
                        out.print(responseHandler(rtn, req));
                        out.close();
                    }

                } else {
                    HJSONObject rtn = new HJSONObject();
                    String desc = e.getMessage();
                    rtn.put("desc", desc);
                    PrintWriter out = response.getWriter();
                    out.print(responseHandler(rtn, req));
                    out.close();
                }

            }

            //判断是否为空指针错误
            if (e instanceof NullPointerException) {
                PrintWriter out = response.getWriter();
                out.print(new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("不是JSON格式").build().toJsonString());
                out.close();
            }
        }

    }


    protected String responseHandler(JSONObject rtn) {
        // TODO Auto-generated method stub
        return rtn.toJSONString();
    }

    protected String responseHandler(JSONObject rtn, ServletRequest request) {
        if (request.getParameter("callback") != null) {
            //将数据填充到callback，并回调
            StringBuffer buf = new StringBuffer();
            buf.append(request.getParameter("callback"));
            buf.append("(");
            buf.append(rtn.toJSONString());
            buf.append(");");
            return buf.toString();
        }
        // TODO Auto-generated method stub
        return rtn.toJSONString();
    }

    public void init(FilterConfig fc) throws ServletException {
        // TODO Auto-generated method stub
    }
}