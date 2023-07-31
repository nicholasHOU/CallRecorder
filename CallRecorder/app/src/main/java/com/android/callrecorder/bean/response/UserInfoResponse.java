package com.android.callrecorder.bean.response;

public class UserInfoResponse extends BaseResponse{

    public DataInfo data;
    /**
     * “name”:”姓名”,
     * // “img”:”头像”, // 废弃
     * “city”:”城市”,
     * “company”:”公司”,
     * “department_big”:”大区”,
     * “department”:”部门”
     */
    public static class DataInfo{
        public String name;
        public String city;
        public String company;
        public String department_big;
        public String department;
    }

}
