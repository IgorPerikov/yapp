package messaging.service

import domain.MessageInput
import messaging.domain.MessageOutput
import messaging.repository.MessagingRepository
import org.springframework.stereotype.Service

@Service
class MessagingService(private val messagingRepository: MessagingRepository) {
    fun sendMessage(msg: MessageInput, from: Int) {
        messagingRepository.save(msg, from)
    }

    fun getLatestMessages(from: Int, to: Int, limit: Int): List<MessageOutput> {
        return messagingRepository.getLatestMessages(from, to, limit)
    }
}
