package com.zhp.facedetect.net.request;

import java.util.ArrayList;

/**
 * Created by zhangpeng on 2017/12/1.
 */

public class NewPersonRequest extends BaseRequest {
    public ArrayList<String> group_ids;
    public String person_id;
    public String image;
    public String person_name;
    public String tag;

    public NewPersonRequest(String person_id, String image) {
        this.person_id = person_id;
        this.image = image;
        group_ids = new ArrayList<>();
        group_ids.add("test");
    }

}
