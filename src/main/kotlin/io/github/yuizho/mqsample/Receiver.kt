package io.github.yuizho.mqsample

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class Receiver {
    // ココ、queueの名前とあわせないとだめそう
    @JmsListener(destination = "mailbox")
    fun receiveMessage(msg: String): Unit {
        // ここで受け取ると、ちゃんとElasticMQから削除されている気がする……
        println(Thread.currentThread().id)
        println("Received: <$msg>")
    }
}