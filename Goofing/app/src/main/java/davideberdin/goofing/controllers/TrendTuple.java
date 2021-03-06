package davideberdin.goofing.controllers;

import android.os.Parcel;
import android.os.Parcelable;

public class TrendTuple implements Parcelable {

    private float[] imageYValues;
    private String[] imageXValues;
    private String title;

    public TrendTuple(float[] y_values, String[] x_values, String title) {
        this.imageYValues = y_values;
        this.imageXValues = x_values;
        this.title = title;
    }

    public float[] getImageYValues() { return imageYValues; }
    public String[] getImageXValues() { return imageXValues; }
    public String getTitle() { return title; }

    //region Parcelable
    public TrendTuple(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<CardTuple> CREATOR = new Parcelable.Creator<CardTuple>() {
        public CardTuple createFromParcel(Parcel in) {
            return new CardTuple(in);
        }

        public CardTuple[] newArray(int size) {
            return new CardTuple[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.imageYValues = in.createFloatArray();
        this.imageXValues = in.createStringArray();
        this.title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(this.imageYValues);
        dest.writeStringArray(this.imageXValues);
        dest.writeString(this.title);
    }
    //endregion
}