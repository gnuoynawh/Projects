package com.gnuoynawh.musical.ticket.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "TB_TICKET", primaryKeys = ["number"])
data class Ticket(

    @NonNull
    @ColumnInfo(name = "number")
    @SerializedName("number")
    var number: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String,

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    var thumb: String,

    @ColumnInfo(name = "place")
    @SerializedName("place")
    var place: String,

    @ColumnInfo(name = "date")
    @SerializedName("date")
    var date: String,

    @ColumnInfo(name = "count")
    @SerializedName("count")
    var count: String,

    @ColumnInfo(name = "state")
    @SerializedName("state")
    var state: String

) : Serializable {
    constructor(): this("", "", "", "", "", "", "")
}