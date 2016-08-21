package rarf.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import rarf.utils.ErrorConfig;
import rarf.utils.HJSONObject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by apple on 15/11/13.
 */

/**
 * @function 資源用於流動的容器
 */
public class ResourceBag {

    //暴露出去的工具類
    public URFPUtils urfpUtils = new URFPUtils();

    public ResultUtils resultUtils = new ResultUtils();


    /* 存放获取到的资源列表*/
    private ConcurrentMap<String, Resource> resources = new ConcurrentHashMap<>();

    /* 该资源流代表的动作*/
    private final Action action;

    /*存放请求数据，构造时候会确保requestData为JSONObject或者JSONArray*/
    private final String requestData;

    private final JSONObject requestDataJSONObject;

    private final JSONArray requestDataJSONArray;

    //用于描述请求的统一资源流动路径
    private final String requestURFP;

    /* 根據URI解析得到的資源映射*/
    private final List<Object[]> uriMapping;

    /* 資源流動的位置，用於在resources中進行索引*/
    //第一個資源下標為0，第二個為1，依次類推
    private AtomicInteger flowIndex;

    /* 存放该资源所有者的userId*/
    private String userId;

    //返回与响应参数
    private final DeferredResult<String> deferredResult;

    //存放处理的结果的JSON对象
    private JSONObject responseJSONObject;

    //存放默认的日志句柄
    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger("com.lf.resourcebag");

    //当前资源包的创建时间戳
    private final Instant resourceBagCreatedTime = Instant.now();


    /**
     * @param builder
     * @function 默认构造函数
     */
    public ResourceBag(Builder builder) {
        this.requestURFP = builder.requestURFP;
        this.action = builder.action;
        this.uriMapping = builder.uriMapping;
        this.requestData = builder.requestData;

        if (HJSONObject.getJsonType(this.requestData) == 0) {
            //如果為JSONObject
            this.requestDataJSONObject = JSONObject.parseObject(requestData) == null ? new JSONObject() : JSONObject.parseObject(requestData);
            this.requestDataJSONArray = new JSONArray();
        } else if (HJSONObject.getJsonType(this.requestData) == 1) {
            this.requestDataJSONObject = new JSONObject();
            this.requestDataJSONArray = JSONArray.parseArray(requestData);
        } else {
            this.requestDataJSONArray = new JSONArray();
            this.requestDataJSONObject = new JSONObject();
        }
        this.userId = builder.userId;
        this.flowIndex = new AtomicInteger(0);
        this.deferredResult = builder.deferredResult;

    }

    /**
     * @Public 方法
     */
    public void print() {
        resources.forEach((k, v) -> {
            v.entityList.forEach(System.out::println);
        });
    }

    /**
     * @function 將流下標加一，意味著流動到下一個資源處理器
     */
    synchronized public void increaseFlowIndex() {
        this.flowIndex.addAndGet(1);
    }

    /**
     * @function 將流下標減一，意味著流動到上一個資源處理器
     */
    synchronized public void decreaseFlowIndex() {
        this.flowIndex.decrementAndGet();
    }

    /**
     * @return
     * @function 獲取當前的資源名
     */
    public String getCurrentResourceName() {

        if ((this.getFlowIndex() > 0)) {
            //如果当前资源处在UriMap中的第一位或者为负数
            return String.valueOf((this.uriMapping.get(this.getFlowIndex()))[0]);
        } else {
            return "empty";
        }

    }

    /**
     * @return
     * @function 獲取當前資源的數量描述
     */
    public String getCurrentResourceQuantifierByDefaultAll() {

        if ((this.getFlowIndex() > -1)) {
            //如果当前资源处在UriMap中的第一位或者为负数
            return ((Optional<String>) ((this.uriMapping.get(this.getFlowIndex())))[1]).orElseGet(() -> {
                return "all";
            });
        } else {
            return "all";
        }

    }


    /**
     * @return
     * @function 获取当前所在资源的上一个资源的名称
     */
    public String getPreResourceName() {

        if (!(this.getFlowIndex() > 0)) {
            //如果当前资源处在UriMap中的第一位或者为负数
            return "empty";
        } else {
            return String.valueOf(
                    this.uriMapping.get(this.getFlowIndex() - 1)
                            [0]);
        }

    }

    public String getPreResourceQuantifierByDefaultAll() {

        if (!(this.getFlowIndex() > 0)) {
            //如果当前资源处在UriMap中的第一位或者为负数
            return "all";
        } else {
            return String.valueOf((
                    (Optional<String>) this.uriMapping.get(this.getFlowIndex() - 1)
                            [1]).orElseGet(() -> {
                return "all";
            }));
        }

    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public Action getAction() {

        //判断是否当前已经到了最后一个，因为Action仅仅对于最后一个资源才有意义，之前的全部为GET
        if (flowIndex.get() == uriMapping.size() - 1) {
            return action;

        } else {
            return Action.GET;
        }

    }

    public List<Object[]> getUriMapping() {
        return uriMapping;
    }

    //為了方便內部調試
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequestData() {
        return requestData;
    }

    public int getFlowIndex() {
        return flowIndex.get();
    }

    public DeferredResult<String> getDeferredResult() {
        return deferredResult;
    }

    public JSONObject getRequestDataJSONObject() {
        return requestDataJSONObject;
    }

    public JSONArray getRequestDataJSONArray() {
        return requestDataJSONArray;
    }

    /**
     * @function Inner Class
     */
    public static class Builder {

        /**
         * @function 该资源流代表的动作
         * @Required
         */
        private final Action action;

        /***
         * @function 根據URI解析得到的資源映射
         * @Required
         */
        private List uriMapping;

        private String requestURFP;

        /**
         * @Required
         */

        /*Optional*/
        private String userId;

        public String requestData;//由於不確定requestData為JSONArray還是JSONObject

        private DeferredResult<String> deferredResult;

        /**
         * @param action
         * @param uriMapping
         * @Deprecated
         * @function 带参构造器
         */
        public Builder(Action action, List uriMapping) {

            this.action = action;

            this.uriMapping = uriMapping;

            //将uriMapping转化为requestURFP

        }

        /**
         * @param action
         * @param requestURFP
         * @function Constructor With RequestAction And Uniform Resource Flow Path
         */
        public Builder(Action action, String requestURFP) {

            this.action = action;

            this.requestURFP = requestURFP;

            //将requestURFP转化为uriMapping
            this.uriMapping = Resource.uriResolver(requestURFP);
        }

        /*构造器区域*/

        /**
         * @param userId
         * @return
         * @function
         */
        public Builder ofUserId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * @param requestData
         * @return
         * @function 組裝請求數據
         */
        public Builder ofRequestData(Optional<String> requestData) {
            if (requestData.isPresent()) {
                this.requestData = requestData.get();
            } else {
                this.requestData = "{}";
            }
            return this;
        }

        public Builder ofDeferredResult(DeferredResult<String> deferredResult) {
            this.deferredResult = deferredResult;
            return this;
        }

        public ResourceBag build() {
            return new ResourceBag(this);
        }
    }

    public static enum Action {
        GET, PUT, DELETE, POST
    }

    /**
     * @param action
     * @return
     * @function 根據字符串解析為具體的動作
     */
    static public Action ActionResolver(String action) {

        action = action.toUpperCase();

        if ("GET".equals(action)) {
            return Action.GET;
        }

        if ("PUT".equals(action)) {
            return Action.PUT;
        }

        if ("DELETE".equals(action)) {
            return Action.DELETE;
        }

        if ("POST".equals(action)) {
            return Action.POST;
        }


        return Action.GET;

    }

    /**
     * @region Public Utils
     */

    /**
     * @return true 表示尚未設置結果 false 表示設置過結果
     * @function 根據當前所屬的資源狀態判斷是否可以設置返回結果
     */
    public boolean isSetResult() {
        if (this.deferredResult != null
                && !this.deferredResult.hasResult()
                && this.flowIndex.get() == (this.uriMapping.size() - 1)) {
            return true;
        }

        return false;
    }

    /**
     * @return
     * @function 獲取本次ResourceBag的處理結果
     */
    @NotNull
    public JSONObject getResultAsJSONObject() {

        //考慮代碼兼容，優先獲取到DeferredResult中的結果
        if (this.deferredResult != null && this.deferredResult.hasResult()) {
            return JSONObject.parseObject(String.valueOf(this.deferredResult.getResult()));
        } else {
            return this.responseJSONObject;
        }

    }

    /**
     * @return
     * @function 判断处理结果是否成功
     */
    public boolean isResultSuccess() {

        JSONObject jsonObject = this.getResultAsJSONObject();

        if (jsonObject.containsKey("code") && jsonObject.get("code").equals(0)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param jsonObject
     * @function
     */
    public void setResultAndResource(@NotNull JSONObject jsonObject) {

    }


    /**
     * @param jsonObject
     * @param resource
     * @function 设置结果
     */
    public void setResultAndResource(@NotNull JSONObject jsonObject, @Nullable Resource resource) {


        //判断是否出现了内部异常
        if (responseJSONObject == null && jsonObject.get("code").equals(ErrorConfig.CODE_INTERNEL_EXCEPTION)) {
            if (this.responseJSONObject == null) {
                //设置响应结果，但是内部异常并不需要反馈给用户
                this.responseJSONObject = jsonObject;
                //如果是錯誤請求，則直接記錄錯誤
                this.LOG.error(this.toString());
            }
            return;
        }

        //设置当前资源
        if (resource != null) {
            //将结果放置到资源中
            this.resources.put(this.getCurrentResourceName(), resource);
        }

        if (this.flowIndex.get() == (this.uriMapping.size() - 1)) {
            //判断是否流动到终点，如果流动到终点才进行赋值
            this.responseJSONObject = jsonObject;

            //判断是否需要设置到DeferredResult中
            if (this.isSetResult()) {
                //如果可以设置，则直接设置返回结果
                this.deferredResult.setResult(jsonObject.toJSONString());

                //只有在返回外部请求结果时，才进行日志记录
                this.LOG.info(this.toString());
            }
        }


    }

    /**
     * @return
     * @function 获取待整合的资源集合，如果没有该参数则为空
     */
    public Set<String> getResourceIntegrationSet() {

        Set<String> set = new HashSet<>();

        if (this.requestDataJSONObject != null && this.requestDataJSONObject.containsKey("resource_integration")) {

            JSONArray.parseArray(String.valueOf(this.requestDataJSONObject.get("resource_integration"))).forEach(o -> {
                set.add(String.valueOf(o));
            });

        }
        return set;
    }

    public static ResourceBag buildResourceFlowForSimpleGet(String[] strings, ResourceBag resourceBagorigin) {

        List<Object[]> uriMapping = new ArrayList<>();

        for (int i = 0; i < strings.length; i = i + 2) {
            uriMapping.add(new Object[]{strings[i], Optional.ofNullable(strings[i + 1])});
        }

        ResourceBag resourceBag = new ResourceBag.Builder(ResourceBag.Action.GET, uriMapping)
                .ofUserId(resourceBagorigin.getUserId())
                .build();

        resourceBag.decreaseFlowIndex();//由於是進行內部流動，則首先將當期位置置為﹣1

        return resourceBag;
    }

    /**
     * @param action            操作类型
     * @param strings           路径符号
     * @param resourceBagorigin 现有的ResourceBag
     * @param jsonString        请求数据：RequestBag
     * @return
     * @function 根据参数动态构造一个ResourceBag
     */
    public static ResourceBag buildResourceBagWithQueryParams(Action action, String[] strings, @NotNull ResourceBag resourceBagorigin, String jsonString) {

        List<Object[]> uriMapping = new ArrayList<>();

        for (int i = 0; i < strings.length; i = i + 2) {

            try {
                uriMapping.add(new Object[]{strings[i], Optional.ofNullable(strings[i + 1])});
            } catch (IndexOutOfBoundsException e) {
                uriMapping.add(new Object[]{strings[i], Optional.ofNullable(null)});

            }

        }

        ResourceBag resourceBag = new ResourceBag.Builder(action, uriMapping)
                .ofUserId(resourceBagorigin.getUserId())
                .ofRequestData(Optional.of(jsonString))
                .build();

        resourceBag.decreaseFlowIndex();//由於是進行內部流動，則首先將當期位置置為﹣1

        return resourceBag;

    }

    /**
     * @param action
     * @param strings
     * @param userId
     * @param jsonString
     * @return
     * @function 根据参数构造出一个可供内部循环的ResourceBag
     */
    public static ResourceBag buildResourceBagWithQueryParamsAndUserId(Action action, String[] strings, @NotNull String userId, String jsonString) {

        List<Object[]> uriMapping = new ArrayList<>();

        for (int i = 0; i < strings.length; i = i + 2) {

            try {
                uriMapping.add(new Object[]{strings[i], Optional.ofNullable(strings[i + 1])});
            } catch (IndexOutOfBoundsException e) {
                uriMapping.add(new Object[]{strings[i], Optional.ofNullable(null)});

            }

        }

        ResourceBag resourceBag = new ResourceBag.Builder(action, uriMapping)
                .ofUserId(userId)
                .ofRequestData(Optional.of(jsonString))
                .build();

        resourceBag.decreaseFlowIndex();//由於是進行內部流動，則首先將當期位置置為﹣1

        return resourceBag;

    }

    @Override
    public String toString() {


        JSONObject jsonObject = new JSONObject();

        //添加当前请求地址
        jsonObject.put("requestURFP", this.requestURFP);

        //添加当前请求方式
        jsonObject.put("requestAction", this.action);

        //添加当前请求参数
        jsonObject.put("requestData", this.requestData);

        //添加当前处理结果
        jsonObject.put("responseData", this.responseJSONObject);

        //添加ResourceBag创建时间
        jsonObject.put("resourceBagAliveTime", Instant.now().getEpochSecond() - this.resourceBagCreatedTime.getEpochSecond());

        return jsonObject.toJSONString();
    }

    /**
     * @region end
     */

    /**
     * @region 内部辅助类
     */

    public class URFPUtils {

    }

    public class ResultUtils {

        public void setResultAndResource(@NotNull JSONObject jsonObject) {
            System.out.print(responseJSONObject.toJSONString());
        }

    }

}
