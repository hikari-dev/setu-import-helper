@file:JvmName("Main")

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.lang.Thread.currentThread
import java.text.SimpleDateFormat
import java.util.*


fun main() {
    val properties =
        Properties().apply { load(currentThread().contextClassLoader.getResourceAsStream("local.properties")) }
    val config = HikariConfig().apply {
        jdbcUrl = properties.getProperty("jdbcUrl")
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = properties.getProperty("username")
        password = properties.getProperty("password")
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    val setuList = parseFile()
    println("start dumping data at ${SimpleDateFormat("HH:mm:ss").format(Date())}")
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(SeTuTable)
        SeTuTable.batchInsert(setuList) { setu ->
            this[SeTuTable.title] = setu.title
            this[SeTuTable.artwork] = setu.artwork
            this[SeTuTable.author] = setu.author
            this[SeTuTable.artist] = setu.artist
            this[SeTuTable.page] = setu.page
            this[SeTuTable.tags] = setu.tags.joinToString()
            this[SeTuTable.type] = setu.type
            this[SeTuTable.filename] = setu.filename
            this[SeTuTable.original] = setu.original
            this[SeTuTable.large] = setu.large
            this[SeTuTable.medium] = setu.medium
            this[SeTuTable.square_medium] = setu.square_medium
        }
    }
    println("finish dumping data at ${SimpleDateFormat("HH:mm:ss").format(Date())}")
}

fun parseFile(): List<SeTu> {
    val lines = File("setu_2020-11-12.json").readLines()
    val datas = mutableListOf<SeTu>()
    for (line in lines) {
        datas.add(
            Json {
                coerceInputValues = true
            }.decodeFromString(line)
        )
    }
    return datas
}

@Serializable
data class SeTu(
    val _id: Int,
    val title: String,
    val artwork: Int,
    val author: String,
    val artist: Int,
    val page: Int,
    val tags: List<String>,
    val type: String = "",
    val filename: String,
    val original: String,
    val large: String,
    val medium: String,
    val square_medium: String,
)

object SeTuTable : IntIdTable() {
    val title = text("title")
    val artwork = integer("artwork")
    val author = text("author")
    val artist = integer("artist")
    val page = integer("page")
    val tags = text("tags")
    val type = text("type")
    val filename = text("filename")
    val original = text("original")
    val large = text("large")
    val medium = text("medium")
    val square_medium = text("square_medium")
}