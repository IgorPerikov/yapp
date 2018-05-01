package messaging

import domain.MessageInput
import messaging.service.MessagingService
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import util.KPostgreSQLContainer


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(initializers = [(MessagingTest.Initializer::class)])
class MessagingTest {
    @Autowired
    private lateinit var messagingService: MessagingService

    @Test
    fun shouldReturnMessagesAfterRequested() {
        val msgFrom = 1
        val msgTo = 2
        val msg0 = MessageInput(text = "text0", from = msgFrom, to = msgTo, fromLogicalId = 0)
        val msg1 = MessageInput(text = "text1", from = msgFrom, to = msgTo, fromLogicalId = 1)
        messagingService.sendMessage(msg0)
        messagingService.sendMessage(msg1)
        val messages = messagingService.getMessagesSince(msgFrom, msgTo, 0, 10)
        assertEquals(1, messages.size)
        val (text, from, to, _, fromLogicalId) = messages[0]
        assertEquals(text, msg1.text)
        assertEquals(from, msg1.from)
        assertEquals(to, msg1.to)
        assertEquals(fromLogicalId, msg1.fromLogicalId)
    }

    @Test
    fun shouldReturnLastMessages() {
        val msgFrom = 10
        val msgTo = 11
        val msg0 = MessageInput(text = "text0", from = msgFrom, to = msgTo, fromLogicalId = 0)
        val msg1 = MessageInput(text = "text1", from = msgFrom, to = msgTo, fromLogicalId = 1)
        val msg2 = MessageInput(text = "text2", from = msgFrom, to = msgTo, fromLogicalId = 1)
        messagingService.sendMessage(msg0)
        messagingService.sendMessage(msg1)
        messagingService.sendMessage(msg2)
        val messages = messagingService.getLatestMessages(msgFrom, msgTo, 5)
        assertEquals(3, messages.size)
        assertEquals(messages[2].text, msg0.text)
        assertEquals(messages[1].text, msg1.text)
        assertEquals(messages[0].text, msg2.text)
    }

    companion object {
        @ClassRule
        @JvmField
        val pg = KPostgreSQLContainer("postgres:10.3")
            .withUsername("root")
            .withPassword("root")
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues
                .of("spring.datasource.url=jdbc:postgresql://${pg.containerIpAddress}:${pg.getMappedPort(5432)}/postgres")
                .applyTo(configurableApplicationContext.environment)
        }
    }
}
