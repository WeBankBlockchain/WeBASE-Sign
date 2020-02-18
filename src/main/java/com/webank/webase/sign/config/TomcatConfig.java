package com.webank.webase.sign.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        // tomcat的配置可以在这里加
        // public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
        tomcatFactory.addConnectorCustomizers(connector -> {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            //设置最大连接数
            protocol.setKeepAliveTimeout(10 * 1000);
            protocol.setMaxKeepAliveRequests(1000);
        });
        return tomcatFactory;
    }
}
