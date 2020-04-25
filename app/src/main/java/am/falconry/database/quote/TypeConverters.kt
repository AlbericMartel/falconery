package am.falconry.database.quote

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TypeConverters {
    private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, FORMATTER) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.let { FORMATTER.format(it) }
    }
}