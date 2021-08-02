package com.noexception.ipcbus.message;

import android.os.Parcel;
import android.os.Parcelable;

public class Responce implements Parcelable {

    // 返回码
    private int resultCode;

    // 数据，一般将对象转json
    private String data;

    // 错误信息
    private String errorMsg;

    public Responce(){}

    protected Responce(Parcel in) {
        resultCode = in.readInt();
        data = in.readString();
        errorMsg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resultCode);
        dest.writeString(data);
        dest.writeString(errorMsg);
    }

    public void readFromParcel(Parcel in) {
        resultCode = in.readInt();
        data = in.readString();
        errorMsg = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Responce> CREATOR = new Creator<Responce>() {
        @Override
        public Responce createFromParcel(Parcel in) {
            return new Responce(in);
        }

        @Override
        public Responce[] newArray(int size) {
            return new Responce[size];
        }
    };

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
