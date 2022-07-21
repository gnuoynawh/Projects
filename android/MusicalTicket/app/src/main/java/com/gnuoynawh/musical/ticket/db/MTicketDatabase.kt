package com.gnuoynawh.musical.ticket.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Ticket::class], version = 1)
abstract class MTicketDatabase: RoomDatabase() {

    // Dao
    abstract fun getTicket(): TicketDao?

    // instance
    companion object {

        @Volatile
        private var INSTANCE: MTicketDatabase? = null

        fun getDatabase(context: Context): MTicketDatabase? {
            if (INSTANCE == null) {
                synchronized(MTicketDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MTicketDatabase::class.java, "ticket_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }

}