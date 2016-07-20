package rarf.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by apple on 15/11/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContextTest.xml"})
public abstract class AbstractHandler<T extends Entity> {

    /**
     * @param resourceBag
     * @return
     * @function 默認的處理方法
     */
    public Observable<ResourceBag> handle(ResourceBag resourceBag) {

        //创建并且返回一个Observable对象
        return Observable.<ResourceBag>create(
                (subscriber) -> {

                    DispatcherController dispatcherController = this.getDispatcherController();

                    //根據不同的Action執行不同的操作
                    Object resultFromInnerRoute = this.preHandle(resourceBag);

                    //判断route方法中执行结果
                    //首先判斷是否為ThrowAble類型
                    if (resultFromInnerRoute instanceof LFThrowable) {

                        //设置本层的错误提示，考慮到結果只會被處理一次，因此會在这里先设置返回结果
                        resourceBag.setResultAndResource(((LFThrowable) resultFromInnerRoute).getJSONObject(), null);

                        //将资源包传递给Reducer
                        this.getDispatcherController().getRelationProxy().combinedReduce(resourceBag).subscribe();

                        //如果是出現了異常，則直接回溯到上一層
                        resourceBag.decreaseFlowIndex();

                        //返回上一层的错误调用
                        subscriber.onError((LFThrowable) resultFromInnerRoute);

                        subscriber.onCompleted();

                    } else {
                        //如果正常执行
                        //判斷是否為終止資源
                        if (resourceBag.getUriMapping().size() > resourceBag.getFlowIndex() + 1) {

                            //如果還未到終止資源
                            dispatcherController.forward(resourceBag)
                                    .subscribe(
                                            //如果下一級執行正常
                                            r -> {

                                                //判断是否已经回溯到第一个资源
                                                if (resourceBag.getFlowIndex() > 0) {
                                                    resourceBag.decreaseFlowIndex();
                                                }
                                                subscriber.onNext(r);
                                                subscriber.onCompleted();
                                            },
                                            //如果下一級執行出錯，則判斷在本級是否需要容錯，一般情况下
                                            throwable -> {

                                                if (!(throwable instanceof LFThrowable)) {
                                                    //打印错误
                                                    throwable.printStackTrace();
                                                }

                                                //此处的错误是代码错误，直接设置错误日志
                                                resourceBag.setResultAndResource(
                                                        (new LFThrowable.Builder().code(ErrorConfig.CODE_INTERNEL_EXCEPTION).desc(throwable.getMessage()).build()).getJSONObject()
                                                        , null);

                                                //判断是否已经回溯到第一个资源
                                                if (resourceBag.getFlowIndex() > 0) {
                                                    resourceBag.decreaseFlowIndex();
                                                }
                                                resourceBag.decreaseFlowIndex();
                                                subscriber.onError(throwable);
                                                subscriber.onCompleted();
                                            });
                        } else {

                            this.getDispatcherController().getRelationProxy().combinedReduce(resourceBag).subscribe();

                            //如果是已經達到了資源流動的終點，則開始回溯
                            //仅仅会作用于终止资源
                            resourceBag.decreaseFlowIndex();
                            subscriber.onNext(resourceBag);
                            subscriber.onCompleted();
                        }
                    }
                }
        );
    }

    protected abstract DispatcherController getDispatcherController();

    /**
     * @param resourceBag 传入的资源包
     * @return true - 已经在route方法中进行了subscriber处理 false - 尚未处理subscriber
     * @function 根据请求的路径与Action的不同进行不同的处理，前置处理
     */
    protected Object preHandle(ResourceBag resourceBag) {
        switch (resourceBag.getAction()) {

            case GET: {
                return this.get(resourceBag);
            }

            case POST: {
                return this.post(resourceBag);
            }

            case PUT: {
                return this.put(resourceBag);
            }

            case DELETE: {
                return this.delete(resourceBag);
            }

            default: {
                return true;
            }
        }
    }


    ;

    /**
     * @param resourceBag
     * @return 如果正常的话，返回true,如果有异常，返回LFThrowable
     * @required
     * @function 处理GET请求，前置業務邏輯處理應該放在getIds函數中，後置業務邏輯處理迴歸到get
     * @segment 判断请求是否有效 -> getIds -> getResourceEntity  -> Wrap Entity in Resource And Put Resource Into ResourceBag
     * -> 判断是否需要资源注入 -> Judge Whether to Set deferred result
     */
    protected abstract Object get(ResourceBag resourceBag);

    //为了方便内部这种注入调用

    /**
     * @param resourceQuantifier 当前资源的描述符
     * @param requestData        请求数据
     * @param ownedResourceBag   已有的ResourceBag
     * @return
     * @function 为了方便直接用类调用的方式来获取资源信息
     */
    public ResourceBag getInvoke(String resourceQuantifier, JSONObject requestData, ResourceBag ownedResourceBag) {

        //构造了一个资源包用于内部流动
        ResourceBag resourceBag = ResourceBag.buildResourceBagWithQueryParams(ResourceBag.Action.GET, new String[]{
                "resource_name", resourceQuantifier
        }, ownedResourceBag, requestData.toJSONString());

        //这样构造出来的资源包一开始下标是-1，将其设置改为0
        resourceBag.increaseFlowIndex();

        //丢给自己的preHandle处理，并且返回处理结果
        return this.handle(resourceBag).toBlocking().first();
    }

    /**
     * @param resourceBag
     * @return
     * @throws LFThrowable 出现异常则抛出
     * @function 建议性，获取所有的ID
     */
    protected abstract JSONArray getIds(ResourceBag resourceBag, List<T> ownedResourceEntityList) throws LFThrowable;

    /**
     * @return
     * @throws LFThrowable 当获取实体为空时则报错
     * @function 建议性，根据ID获取所有的资源
     * @segment 遍历jsonArray获取所有尚未拥有的资源编号 -> 根据编号获取资源列表
     */
    protected abstract List<T> getResourceEntity(JSONArray jsonArray, ResourceBag resourceBag, List<T> ownedResourceEntityList) throws LFThrowable;

    /**
     * @param entityList
     * @param resourceName
     * @function 根据用户请求自动完成资源的注入
     */
    protected void resourceIntegration(List<T> entityList, String resourceName) {

    }

    /**
     * @param resourceBag
     * @return 如果正常的话，返回true,如果有异常，返回LFThrowable
     * @function 处理POST请求
     */
    protected abstract Object post(ResourceBag resourceBag);


    /**
     * @param resourceBag
     * @return
     * @function 处理单个的创建请求
     */
    protected abstract void postSingle(ResourceBag resourceBag) throws LFThrowable;

    /**
     * @param resourceBag
     * @throws LFThrowable
     * @function 处理多个创建的请求
     */
    protected abstract void postMultiple(ResourceBag resourceBag) throws LFThrowable;

    /**
     * @param resourceBag
     * @return
     * @function 处理所有的put请求
     * @segment 判断是否有操作权限 - 执行操作 - 返回结果
     */
    protected abstract Object put(ResourceBag resourceBag);

    /**
     * @param resourceBag
     * @return
     * @function 处理所有的post请求
     */
    protected abstract Object delete(ResourceBag resourceBag);

    /**
     * @param resourceBag
     * @function 判斷是否需要在本次處理中設置返回值，即設置deferredResult的返回值
     * @segment 判斷是否為資源終點->判斷是否有deferredResult->判斷deferredResult是否有設置值
     * -> 判断是否需要进行资源注入，如果需要进行资源注入则构造内部的ResourceBag进行资源获取并且注入
     */
    protected abstract void setResult(ResourceBag resourceBag) throws LFThrowable;

    /**
     * @param resourceBag
     * @return
     * @function 后置处理
     */
    protected abstract Object postHandle(ResourceBag resourceBag);

    /**
     * @region PUBLIC API
     */
    /**
     * @return
     * @function 构造一次内部回环的资源流，单个资源
     */
    public ResourceBag launchResourceFlowForSimpleGet(String[] strings, ResourceBag resourceBagorigin) {

        List<Object[]> uriMapping = new ArrayList<>();

        for (int i = 0; i < strings.length; i = i + 2) {
            uriMapping.add(new Object[]{strings[i], Optional.ofNullable(strings[i + 1])});
        }

        ResourceBag resourceBag = new ResourceBag.Builder(ResourceBag.Action.GET, uriMapping)
                .ofUserId(resourceBagorigin.getUserId())
                .build();

        resourceBag.decreaseFlowIndex();//由於是進行內部流動，則首先將當期位置置為﹣1

        return getDispatcherController().forward(resourceBag).toBlocking().first();

    }

    /**
     * @region TEST
     */
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;


    @org.junit.Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


}
