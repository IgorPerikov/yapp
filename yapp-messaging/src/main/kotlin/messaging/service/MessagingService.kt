package messaging.service

import domain.MessageInput
import messaging.domain.MessageOutput
import messaging.repository.MessagingRepository
import org.springframework.stereotype.Service

@Service
class MessagingService(private val messagingRepository: MessagingRepository) {
    fun sendMessage(msg: MessageInput, sender: Int) {
        messagingRepository.save(msg, sender)
    }

    fun getLatestMessages(user1: Int, user2: Int, limit: Int): List<MessageOutput> {
        return messagingRepository.getLatestMessages(user1, user2, limit)
    }
}
