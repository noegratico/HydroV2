package com.example.aqua_v2.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TempModel implements Parcelable {
    private ArrayList<TemperatureSensor> temperatureSensors = new ArrayList<>();

    public List<TemperatureSensor> getTemperatureSensors() {
        return temperatureSensors;
    }

    public void setTemperatureSensors(ArrayList<TemperatureSensor> temperatureSensors) {
        this.temperatureSensors = temperatureSensors;
    }

    public TempModel() {}

    protected TempModel(Parcel in) {
      temperatureSensors =  in.readArrayList(TemperatureSensor.class.getClassLoader());
    }

    public static final Creator<TempModel> CREATOR = new Creator<TempModel>() {
        @Override
        public TempModel createFromParcel(Parcel in) {
            return new TempModel(in);
        }

        @Override
        public TempModel[] newArray(int size) {
            return new TempModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(temperatureSensors);
    }
}
