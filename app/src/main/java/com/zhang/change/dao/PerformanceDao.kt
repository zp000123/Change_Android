package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Performance

@Dao
interface PerformanceDao {
    @Query("SELECT * from performance")
    suspend fun queryAll():List<Performance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: Performance)

    @Delete
    suspend fun delete(data: Performance)
}