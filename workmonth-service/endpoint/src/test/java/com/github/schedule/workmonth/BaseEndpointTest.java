package com.github.schedule.workmonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { MappingJackson2HttpMessageConverter.class })
public class BaseEndpointTest {

    protected MockMvc mockMvc;

    @Autowired
    protected MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

}
