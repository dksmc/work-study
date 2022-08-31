package com.dk.study;

import com.dk.study.core.mq.rabbitmq.RabbitMqComponentFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author dk
 */
@SpringBootApplication
@ComponentScan(value = {"com.dk"},excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {RabbitMqComponentFilter.class})})
public class WorkStudyApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WorkStudyApplication.class, args);
    }
}
