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
    fun shouldReturnLastMessages() {
        val msgFrom = 10
        val msgTo = 11
        val msg0 = MessageInput("text0", msgTo)
        val msg1 = MessageInput("text1", msgTo)
        val msg2 = MessageInput("text2", msgTo)
        messagingService.sendMessage(msg0, msgFrom)
        messagingService.sendMessage(msg1, msgFrom)
        messagingService.sendMessage(msg2, msgFrom)
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
