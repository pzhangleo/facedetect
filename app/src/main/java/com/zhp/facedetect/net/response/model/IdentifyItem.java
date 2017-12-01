package com.zhp.facedetect.net.response.model;

import com.being.base.http.model.BaseObject;

/**
 * Created by zhangpeng on 2017/11/30.
 */

public class IdentifyItem implements BaseObject {
    public String person_id;
    public String face_id;
    public String confidence;
    public String tag;
}
