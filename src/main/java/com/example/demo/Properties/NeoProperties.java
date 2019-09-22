package com.example.demo.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置类
 */
@Component
public class NeoProperties {


    @Value("${com.neo.title}")
    private String title;
    @Value("${com.neo.description}")
    private String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}