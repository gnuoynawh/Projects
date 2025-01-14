package com.gnuoynawh.exam.ticketcrawlingexam.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gnuoynawh.exam.ticketcrawlingexam.db.dao.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.db.dao.TicketDao

@Database(entities = [Ticket::class], version = 1)
abstract class TicketDatabase: RoomDatabase() {

    // Dao
    abstract fun getTicket(): TicketDao?

    // instance
    companion object {

        @Volatile
        private var INSTANCE: TicketDatabase? = null

        fun getDatabase(context: Context): TicketDatabase? {
            if (INSTANCE == null) {
                synchronized(TicketDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, TicketDatabase::class.java, "ticket_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }

}