package com.jmsgoodies.example.spring;

import com.jmsgoodies.JMSPoster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean(name="jmsPoster")
    public JMSPoster jmsPoster(){
        JMSPoster jmsPoster = JMSPoster.getInstance();
        return jmsPoster;
    }
}
