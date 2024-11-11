package eu.mcomputng.mobv.zadanie.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eu.mcomputng.mobv.zadanie.data.db.entities.UserEntity

@Dao
interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserItems(items: List<UserEntity>)

    @Query("select * from users where id=:id")
    fun getUserItem(id: String): LiveData<UserEntity?>

    @Query("select * from users")
    fun getUsers(): LiveData<List<UserEntity>?>

    @Query("delete from users")
    suspend fun deleteUserItems()

}