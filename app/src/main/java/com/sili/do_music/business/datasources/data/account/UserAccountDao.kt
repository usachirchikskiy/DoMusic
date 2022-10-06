package com.sili.do_music.business.datasources.data.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sili.do_music.business.model.main.TeacherAccount

@Dao
interface UserAccountDao {

    @Query("""UPDATE user_account SET phone = :phone WHERE id = :id""")
    suspend fun changeUserPhone(phone: String,id:Int)

    @Query("""SElECT *FROM user_account LIMIT 1""")
    suspend fun getUserAccount(): TeacherAccount

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(userAccount: TeacherAccount): Long

    @Query("DELETE FROM user_account")
    suspend fun deleteUserAccount()
}