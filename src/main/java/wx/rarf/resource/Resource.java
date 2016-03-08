package wx.rarf.resource;


import wx.rarf.resource.entity.Entity;

import java.util.*;

/**
 * Created by apple on 15/11/13.
 */
public class Resource<T extends Entity> {

    public Map<String, Object> attributes = new HashMap<>();

    public final List<T> entityList;

    /**
     * @param entityList 传入的实体列表
     * @function 带参构造函数
     */
    public Resource(List<T> entityList) {

        this.entityList = entityList;

    }

    public Resource(T entity) {

        this.entityList = new ArrayList<>();

        this.entityList.add(entity);

    }

    public static void print(Resource resource) {

        resource.attributes.forEach(((s, o) -> {

            System.out.print(s + ":");

            Arrays.asList(o).stream().forEach(System.out::print);

            System.out.println();


        }));
    }

    /**
     * @param uri
     * @return
     * @function 将Uri解析到资源映射中
     */
    public static List<Object[]> uriResolver(String uri) {

        ArrayList<Object[]> result = new ArrayList<>();

        String[] temp = uri.split("\\?")[0].split("/");

        int flag = 1;

        while (true) {
            //判斷當前位置是否存在資源
            if (temp.length > flag && temp.length > flag + 1) {

                //如果存在资源，并且包含有资源的ID，则将其添加到数组中
                result.add(new Object[]{temp[flag], Optional.ofNullable(temp[flag + 1])});

                flag = flag + 2;

                continue;

            } else if (temp.length > flag) {

                //如果存在资源，且是终止资源，则只添加自己的名称
                result.add(new Object[]{temp[flag], Optional.ofNullable(null)});

                break;

            } else {
                break;
            }

        }

        return result;

    }

    /**
     * @param list
     * @function 打印輔助
     */
    public static void uriMappingPrinter(List<Object[]> list) {

        list.stream().forEach((objects -> {

            Arrays.asList(objects).stream().forEach(o -> {
                System.out.print(o.toString() + ",");
            });

            System.out.println();
        }));

    }


}
