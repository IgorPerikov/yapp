package messaging.service

import domain.MessageInput
import messaging.domain.MessageOutput
import messaging.repository.MessagingRepository
import org.springframework.stereotype.Service

@Service
class MessagingService(private val messagingRepository: MessagingRepository) {
    fun sendMessage(msg: MessageInput) {
        messagingRepository.save(msg)
    }

    fun getMessagesSince(from: Int, to: Int, after: Int, limit: Int): List<MessageOutput> {
        return messagingRepository.getMessagesSince(from, to, after, limit)
    }

    fun getLatestMessages(from: Int, to: Int, limit: Int): List<MessageOutput> {
        return messagingRepository.getLatestMessages(from, to, limit)
    }
}
