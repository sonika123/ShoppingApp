package com.yardbird.justice.yardbird.Interfaces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Justice on 17/02/2017.
 */

public class Users implements Serializable {

    @SerializedName ("CustomerID")
    public int id;

    @SerializedName("FName")
    public String FName;

    @SerializedName("LName")
    public String LName;

    @SerializedName("UserName")
    public String username;

    @SerializedName("Email")
    public String email;

}
