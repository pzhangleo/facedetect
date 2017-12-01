package com.zhp.facedetect.net.response;

import java.util.ArrayList;

/**
 * Created by zhangpeng on 2017/12/1.
 */

public class NewPersonResponse extends BaseResponse {
    public int suc_group;
    public int suc_face;
    public String person_id;
    public String face_id;
    public ArrayList<String> group_ids;
}
