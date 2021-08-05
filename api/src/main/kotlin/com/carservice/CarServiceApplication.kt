package com.carservice

import com.carservice.tasks.CarNameSchedulingService
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import java.util.TimeZone

@SpringBootApplication
@EnableCaching
class CarServiceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    val app = runApplication<CarServiceApplication>(*args)

    // updates car names upon application startup
    val schedulingService = app.getBean<CarNameSchedulingService>()
    schedulingService.updateCarNames()

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zagreb"))
    println("Application started successfully!")
}
