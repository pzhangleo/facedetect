package com.zhp.facedetect.net.response;

import com.being.base.http.model.BaseObject;

/**
 * Created by zhangpeng on 2017/11/30.
 */

public class BaseResponse implements BaseObject{
    public String session_id;
    public int errorcode;
    public String errormsg;

    public boolean isSuc() {
        return errorcode == 0;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "session_id='" + session_id + '\'' +
                ", errorcode=" + errorcode +
                ", errormsg='" + errormsg + '\'' +
                '}';
    }
}
