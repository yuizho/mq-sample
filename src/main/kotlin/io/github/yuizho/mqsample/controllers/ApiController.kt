package io.github.yuizho.mqsample.controllers

import org.springframework.jms.core.JmsTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController(val jmsTemplate: JmsTemplate) {

    @GetMapping("/get")
    fun get(): String {
        println(Thread.currentThread().id)
        println("Sending an email message.")
        println("jmsTemplate instance: ${System.identityHashCode(jmsTemplate)}")
        jmsTemplate.convertAndSend("mailbox", "Hi!!")
        return "Hi!"
    }
}
