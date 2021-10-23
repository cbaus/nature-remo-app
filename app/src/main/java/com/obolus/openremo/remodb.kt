package com.obolus.openremo

import android.content.Context
import androidx.room.*

@Entity
data class Measurement(
    @PrimaryKey val time: Long,
    @ColumnInfo(name = "temp") val temp: Float,
    @ColumnInfo(name = "humid") val humid: Int
)

@TypeConverter
fun measurementsToLists(measures: List<Measurement>): DataLists {
    val temps: MutableList<Float> = arrayListOf()
    val humidities: MutableList<Int> = arrayListOf()
    val times: MutableList<Long> = arrayListOf()
    for (iM in measures) {
        temps.add(iM.temp)
        humidities.add(iM.humid)
        times.add(iM.time)
    }
    return DataLists(temps.toFloatArray(), humidities.toIntArray(), times.toLongArray())
}

data class DataLists(
    var temps: FloatArray,
    var humidities: IntArray,
    var times: LongArray
)

@Dao
interface MeasurementDao {
//    @Query("SELECT * FROM measurement")
//    fun getAll(): List<Measurement>
//
//    @Query("SELECT `temp` FROM (SELECT `time`, `temp` FROM measurement WHERE `time` > :start ORDER BY `time` DESC LIMIT 20) ORDER BY `time` ASC")
//    fun getLatestTemps(start: Long): FloatArray
//
//    @Query("SELECT `humid` FROM (SELECT `time`, `humid` FROM measurement WHERE `time` > :start ORDER BY `time` DESC LIMIT 20) ORDER BY `time` ASC")
//    fun getLatestHumidities(start: Long): IntArray

    @Query("SELECT `time`, `temp`, `humid` FROM (SELECT * FROM measurement WHERE `time` > :start ORDER BY `time` DESC LIMIT 20) ORDER BY `time` ASC")
    fun getLatest(start: Long): List<Measurement>

    @Insert
    fun insert(vararg measurement: Measurement)

    @Delete
    fun delete(user: Measurement)
}

//should remove from main thread
@Database(entities = [Measurement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurmentDao(): MeasurementDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    println("Building database")
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "remo-db"
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}