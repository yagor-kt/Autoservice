package com.example.autoservice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Calendar

@Database(
    entities = [
        Auto::class,
        Request::class,
        RequestItem::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun autoDao(): AutoDao
    abstract fun requestDao(): RequestDao
    abstract fun requestItemDao(): RequestItemDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autoservice.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

private class SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val now = System.currentTimeMillis()
        val lastMonthFirst = timestampInPreviousMonth(day = 10, hour = 10)
        val lastMonthSecond = timestampInPreviousMonth(day = 20, hour = 14)
        val recentFirst = timestampDaysAgo(2, hour = 11)
        val recentSecond = timestampDaysAgo(1, hour = 15)
        val recentThird = timestampDaysAgo(0, hour = 9)

        val autos = listOf(
            arrayOf<Any>(1L, "А123ВС77", "Toyota Camry", "JTNB11HK303012345", 82500, "Иван Петров", "+7 900 111-22-33"),
            arrayOf<Any>(2L, "К456МН77", "Kia Rio", "Z94C251BBLR123456", 43100, "Мария Смирнова", "+7 900 222-33-44"),
            arrayOf<Any>(3L, "Е789ОР99", "Hyundai Solaris", "Z94K241CBJR234567", 67200, "Алексей Иванов", "+7 900 333-44-55"),
            arrayOf<Any>(4L, "М321ТА50", "Lada Vesta", "XTAGFL110MY345678", 38900, "Ольга Кузнецова", "+7 900 444-55-66"),
            arrayOf<Any>(5L, "В654УХ77", "BMW X5", "WBAKR01020L456789", 105000, "Дмитрий Соколов", "+7 900 555-66-77")
        )

        autos.forEach { row ->
            db.execSQL(
                """
                INSERT INTO autos
                (id, plateNumber, brandModel, vin, mileage, ownerName, ownerPhone)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                row
            )
        }

        val requests = listOf(
            arrayOf<Any?>(1L, 1L, "А123ВС77", "Toyota Camry", "Иван Петров", "ACCEPTED", now - 3_600_000L, null),
            arrayOf<Any?>(2L, 2L, "К456МН77", "Kia Rio", "Мария Смирнова", "DIAGNOSTICS", now - 7_200_000L, null),
            arrayOf<Any?>(3L, 3L, "Е789ОР99", "Hyundai Solaris", "Алексей Иванов", "REPAIR", now - 86_400_000L, null),
            arrayOf<Any?>(4L, 1L, "А123ВС77", "Toyota Camry", "Иван Петров", "CLOSED", lastMonthFirst - 3_600_000L, lastMonthFirst),
            arrayOf<Any?>(5L, 2L, "К456МН77", "Kia Rio", "Мария Смирнова", "CLOSED", lastMonthSecond - 3_600_000L, lastMonthSecond),
            arrayOf<Any?>(6L, 3L, "Е789ОР99", "Hyundai Solaris", "Алексей Иванов", "CLOSED", recentFirst - 3_600_000L, recentFirst),
            arrayOf<Any?>(7L, 4L, "М321ТА50", "Lada Vesta", "Ольга Кузнецова", "CLOSED", recentSecond - 3_600_000L, recentSecond),
            arrayOf<Any?>(8L, 5L, "В654УХ77", "BMW X5", "Дмитрий Соколов", "CLOSED", recentThird - 3_600_000L, recentThird)
        )

        requests.forEach { row ->
            db.execSQL(
                """
                INSERT INTO requests
                (id, autoId, plateNumber, brandModel, ownerName, status, createdAt, closedAt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                row
            )
        }

        val items = listOf(
            arrayOf<Any>(1L, 4L, "WORK", "Замена масла", 1500),
            arrayOf<Any>(2L, 4L, "PART", "Масляный фильтр", 800),
            arrayOf<Any>(3L, 4L, "WORK", "Диагностика подвески", 2000),

            arrayOf<Any>(4L, 5L, "WORK", "Замена тормозных колодок", 3500),
            arrayOf<Any>(5L, 5L, "PART", "Тормозные колодки", 2800),

            arrayOf<Any>(6L, 6L, "WORK", "Компьютерная диагностика", 1800),
            arrayOf<Any>(7L, 6L, "PART", "Свечи зажигания (4 шт.)", 1600),

            arrayOf<Any>(8L, 7L, "WORK", "Шиномонтаж", 2500),
            arrayOf<Any>(9L, 7L, "PART", "Щётки стеклоочистителя", 900),

            arrayOf<Any>(10L, 8L, "WORK", "Замена ремня ГРМ", 8000),
            arrayOf<Any>(11L, 8L, "PART", "Ремень ГРМ", 5500),
            arrayOf<Any>(12L, 8L, "WORK", "Замена свечей зажигания", 2200)
        )

        items.forEach { row ->
            db.execSQL(
                """
                INSERT INTO request_items (id, requestId, type, title, price)
                VALUES (?, ?, ?, ?, ?)
                """.trimIndent(),
                row
            )
        }

        val reminders = listOf(
            arrayOf<Any>(1L, 1L, "А123ВС77", "Toyota Camry", timestampDaysFromNow(-2)),
            arrayOf<Any>(2L, 2L, "К456МН77", "Kia Rio", timestampDaysFromNow(5)),
            arrayOf<Any>(3L, 3L, "Е789ОР99", "Hyundai Solaris", timestampDaysFromNow(45))
        )

        reminders.forEach { row ->
            db.execSQL(
                """
                INSERT INTO reminders (id, autoId, plateNumber, brandModel, dueDate)
                VALUES (?, ?, ?, ?, ?)
                """.trimIndent(),
                row
            )
        }
    }
}

private fun timestampDaysAgo(days: Int, hour: Int): Long =
    Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -days)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun timestampDaysFromNow(days: Int): Long =
    Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, days)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun timestampInPreviousMonth(day: Int, hour: Int): Long =
    Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, minOf(day, getActualMaximum(Calendar.DAY_OF_MONTH)))
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis