package messaging.repository

import messaging.domain.MessageInput
import messaging.domain.MessageOutput
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MessagingRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun save(msg: MessageInput) {
        jdbcTemplate.update(
            "INSERT INTO messages(text, \"from\", \"to\", from_logical_id) " +
                    "VALUES (:text, :from, :to, :from_logical_id)",
            MapSqlParameterSource()
                .addValue("text", msg.text)
                .addValue("from", msg.from)
                .addValue("to", msg.to)
                .addValue("from_logical_id", msg.fromLogicalId)
        )
    }

    fun getMessagesSince(from: Int, to: Int, after: Int, limit: Int): List<MessageOutput> {
        return jdbcTemplate.query(
            "SELECT * FROM messages " +
                    "WHERE \"from\" = :from " +
                    "AND \"to\" = :to " +
                    "AND from_logical_id > :after " +
                    "ORDER BY id ASC " +
                    "LIMIT :limit",
            MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("after", after)
                .addValue("limit", limit),
            ::toMessage
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

    private fun toMessage(rs: ResultSet, rowNum: Int): MessageOutput {
        return MessageOutput(
            rs.getString("text"),
            rs.getInt("from"),
            rs.getInt("to"),
            rs.getTimestamp("date").toInstant(),
            rs.getInt("from_logical_id")
        )
    }
}
