package com.zhp.facedetect.net.request;

import com.being.base.http.GsonManager;
import com.being.base.http.model.BaseObject;
import com.google.gson.Gson;
import com.zhp.facedetect.net.Config;

/**
 * Created by zhangpeng on 2017/12/1.
 */

public class BaseRequest implements BaseObject {
    public String app_id = Config.app_id;

    @Override
    public String toString() {
        return GsonManager.getGson().toJson(this);
    }
}
