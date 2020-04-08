package com.ola.travel.camera.bean;


import java.util.ArrayList;

/**
 * @author zhangzheng
 * @Date 2019/3/11 5:32 PM
 * @ClassName MultipleImgUploadBean
 * <p>
 * Desc :
 */
public class MultipleImgUploadBean {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private ArrayList<String> url;

        public ArrayList<String> getUrl() {
            return url;
        }

        public void setUrl(ArrayList<String> url) {
            this.url = url;
        }
    }
}
