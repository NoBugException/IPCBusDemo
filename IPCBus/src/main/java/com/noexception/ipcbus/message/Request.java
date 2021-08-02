package com.noexception.ipcbus.message;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {

    // 请求名称
    private String requestName;

    // 数据（json）
    private String paramJson;

    public Request() {}

    public Request(String requestName, String paramJson, String serviceClassFullName) {
        this.requestName = requestName;
        this.paramJson = paramJson;
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            Request request = new Request();
            request.requestName = in.readString();
            request.paramJson = in.readString();
            return request;
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestName);
        dest.writeString(paramJson);
    }

    public void readFromParcel(Parcel in) {
        this.requestName = in.readString();
        this.paramJson = in.readString();
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
