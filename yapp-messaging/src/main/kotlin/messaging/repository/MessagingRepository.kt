package messaging.repository

import domain.MessageInput
import messaging.domain.MessageOutput
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MessagingRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun save(msg: MessageInput, from: Int) {
        jdbcTemplate.update(
            "INSERT INTO messages(text, \"from\", \"to\") " +
                    "VALUES (:text, :from, :to)",
            MapSqlParameterSource()
                .addValue("text", msg.text)
                .addValue("from", from)
                .addValue("to", msg.to)
        )
    }

    fun getLatestMessages(from: Int, to: Int, limit: Int): List<MessageOutput> {
        return jdbcTemplate.query(
            "SELECT * FROM messages " +
                    "WHERE \"from\" = :from " +
                    "AND \"to\" = :to " +
                    "ORDER BY id DESC " +
                    "LIMIT :limit",
            MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
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
