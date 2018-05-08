package messaging.controller

import domain.MessageInput
import domain.USER_ID_HEADER_NAME
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import messaging.domain.MessageOutput
import messaging.service.MessagingService
import org.springframework.web.bind.annotation.*

@RestController
class MessagingController(private val messagingService: MessagingService) {
    @ApiOperation("Send a new message", notes = "Remember that it's up to client to put the proper logical id")
    @PostMapping("/messages")
    fun sendMessage(@RequestHeader(USER_ID_HEADER_NAME) from: Int, @RequestBody msg: MessageInput) {
        messagingService.sendMessage(msg, from)
    }

    @ApiOperation("Get all messages with a logical id higher than requested")
    @GetMapping("/messages")
    fun getMessages(
        @RequestHeader(USER_ID_HEADER_NAME) from: Int,
        @RequestParam to: Int,
        @ApiParam("Logical id of message that was associated by sender", required = true) @RequestParam after: Int,
        @ApiParam("Limit, default to 10") @RequestParam(required = true, defaultValue = "10") limit: Int
    ): List<MessageOutput> {
        return messagingService.getMessagesSince(from, to, after, limit)
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
