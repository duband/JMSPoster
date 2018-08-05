package com.github.duband.jmsgoodies.example.spring;

import com.github.duband.jmsgoodies.JMSPoster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

@Configuration
public class AppConfig {

    @Bean(name="jmsPoster")
    public JMSPoster jmsPoster(){
        JMSPoster jmsPoster = JMSPoster.getInstance();
        return jmsPoster;
    }
}
