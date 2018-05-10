package messaging.repository

import domain.MessageInput
import messaging.domain.MessageOutput
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MessagingRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun save(msg: MessageInput, sender: Int) {
        jdbcTemplate.update(
            "INSERT INTO messages(text, \"from\", \"to\") " +
                    "VALUES (:text, :from, :to)",
            MapSqlParameterSource()
                .addValue("text", msg.text)
                .addValue("from", sender)
                .addValue("to", msg.to)
        )
    }

    fun getLatestMessages(user1: Int, user2: Int, limit: Int): List<MessageOutput> {
        return jdbcTemplate.query(
            "SELECT * FROM messages " +
                    "WHERE (\"from\" = :from AND \"to\" = :to) OR (\"from\" = :to AND \"to\" = :from)" +
                    "ORDER BY id DESC " +
                    "LIMIT :limit",
            MapSqlParameterSource()
                .addValue("from", user1)
                .addValue("to", user2)
                .addValue("limit", limit),
            ::toMessage
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun toMessage(rs: ResultSet, rowNum: Int): MessageOutput {
        return MessageOutput(
            rs.getString("text"),
            rs.getInt("from"),
            rs.getInt("to"),
            rs.getTimestamp("date").toInstant()
        )
    }
}
