package wx.rarf.utils.throwable;


public class RequestException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int code;
    private String desc;
    private int subCode = -1;


    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public int getSubCode() {
        return subCode;
    }

    public void setSubCode(int subCode) {
        this.subCode = subCode;
    }


    public String getDesc() {
        return desc;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }

    public RequestException(int code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
    }

    public RequestException(int code, int subCode, String desc) {
        //super();
        this.code = code;
        this.desc = desc;
        this.subCode = subCode;
    }


    public RequestException(String exceptionDesc, int code, String desc) {
        super(exceptionDesc);
        this.code = code;
        this.desc = desc;
    }


}
