package com.example.mobileprojectfinal.movies.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "investment") var investment: Int,
    @ColumnInfo(name = "releaseDate") var releaseDate: String,
    @ColumnInfo(name = "hasSequel") var hasSequel: Boolean,
    @ColumnInfo(name = "owner") var owner: String?,
    @ColumnInfo(name = "action") var action: String?,
    @ColumnInfo(name = "attemptUpdateAt") var attemptUpdateAt: Long,
    @ColumnInfo(name = "picturePath") var picturePath: String?,
    @ColumnInfo(name = "latitude") var latitude: Float?,
    @ColumnInfo(name = "longitude") var longitude: Float?
) : Parcelable {
    override fun toString(): String = "$title $investment $releaseDate $hasSequel"

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = kotlin.math.floor(releaseDate.toDouble())
        return sdf.format(netDate)
    }
}

data class MovieDTO(
    var title: String,
    var investment: Int,
    var releaseDate: String,
    var hasSequel: Boolean,
    var owner: String?
)