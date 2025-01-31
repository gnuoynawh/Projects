package com.gnuoynawh.musical.ticket.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @WorkerThread
    suspend fun insert(vararg ticket: Ticket)

    @Query("select * from TB_TICKET order by date desc")
    fun getAllTickets(): LiveData<List<Ticket>>

    @Query("delete from TB_TICKET where number = :number")
    suspend fun delete(number: String)

}