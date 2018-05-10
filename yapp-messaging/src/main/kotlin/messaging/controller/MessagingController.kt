package messaging.controller

import domain.MessageInput
import domain.USER_ID_HEADER_NAME
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import messaging.domain.MessageOutput
import messaging.service.MessagingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import javax.servlet.http.HttpServletResponse

@RestController
class MessagingController(private val messagingService: MessagingService) {
    private val hostname = InetAddress.getLocalHost().hostName

    @ApiOperation("Sends a new message")
    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun sendMessage(
        @RequestHeader(USER_ID_HEADER_NAME) sender: Int,
        @RequestBody msg: MessageInput,
        response: HttpServletResponse
    ) {
        response.setHeader("X-Hostname", hostname)
        messagingService.sendMessage(msg, sender)
    }

    @ApiOperation("Returns `limit` latest messages in the chat")
    @GetMapping("/latest-messages")
    fun getLatestMessages(
        @RequestHeader(USER_ID_HEADER_NAME) from: Int,
        @RequestParam to: Int,
        @ApiParam("Limit, default to 10") @RequestParam(required = true, defaultValue = "10") limit: Int
    ): List<MessageOutput> {
        return messagingService.getLatestMessages(from, to, limit)
    }
}
