package wx.webservice.hessian.server;

/**
 * Created by apple on 16/3/16.
 */

import java.io.Serializable;

public class Foo implements Serializable {

    private String name;

    private int x;

    public Foo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}