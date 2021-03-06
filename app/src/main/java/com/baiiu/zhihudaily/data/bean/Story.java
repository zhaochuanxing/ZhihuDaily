package com.baiiu.zhihudaily.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.baiiu.zhihudaily.newsList.DailyNewsAdapter;
import java.util.List;

/**
 * author: baiiu
 * date: on 16/4/5 15:22
 * description:
 */
public class Story implements Parcelable {
    public List<String> images;
    private int type;
    public long id;
    public String ga_prefix;
    public String title;
    public String date;
    public boolean isRead;

    public int mType = DailyNewsAdapter.TYPE_NEWS;

    public Story() {
    }

    public Story(long id, List<String> images, String title) {
        this.images = images;
        this.id = id;
        this.title = title;
    }


    @Override public String toString() {
        return "Story{" +
                "type=" + type +
                ", id=" + id +
                ", date='" + date + '\'' +
                ", mType=" + mType +
                '}';
    }

    protected Story(Parcel in) {
        images = in.createStringArrayList();
        type = in.readInt();
        id = in.readLong();
        ga_prefix = in.readString();
        title = in.readString();
        isRead = in.readByte() != 0;
        mType = in.readInt();
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(images);
        dest.writeInt(type);
        dest.writeLong(id);
        dest.writeString(ga_prefix);
        dest.writeString(title);
        dest.writeByte((byte) (isRead ? 1 : 0));
        dest.writeInt(mType);
    }
}
