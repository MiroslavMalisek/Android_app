package eu.mcomputng.mobv.zadanie.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val updated: String,
    val lat: Double,
    val lon: Double,
    val radius: Double,
    val photo: String = ""
)