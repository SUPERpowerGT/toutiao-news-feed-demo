package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xuziyi.toutiaoandroid.data.local.entity.AuthorEntity

@Dao
interface AuthorDao {

    @Query("SELECT * FROM authors WHERE id = :id LIMIT 1")
    suspend fun getAuthorById(id: Long): AuthorEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Query("DELETE FROM authors")
    suspend fun clearAll()
}
