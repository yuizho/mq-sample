package io.github.yuizho.mqsample

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class Receiver {
    @JmsListener(destination = "mailbox")
    fun receiveMessage(msg: String): Unit {
        println(Thread.currentThread().id)
        println("Received: <$msg>")
    }
}