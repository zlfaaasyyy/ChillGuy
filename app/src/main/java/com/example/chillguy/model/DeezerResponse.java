package com.example.chillguy.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DeezerResponse {
    @SerializedName("data")
    private List<Track> data;

    @SerializedName("total")
    private int total;

    public List<Track> getData()  { return data; }
    public int         getTotal() { return total; }
}