package com.zhp.facedetect.net.response;

import com.being.base.http.model.BaseObject;
import com.zhp.facedetect.net.response.model.IdentifyItem;

import java.util.ArrayList;

/**
 * Created by zhangpeng on 2017/11/30.
 */

public class FaceidentifyResponse extends BaseResponse {

    public ArrayList<IdentifyItem> candidates;

    @Override
    public String toString() {
        return "FaceidentifyResponse{" +
                "candidates=" + candidates +
                "} " + super.toString();
    }
}
