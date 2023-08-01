package com.udacity.pricing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceUnitTests {
    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllPrices() throws Exception {
        mockMvc.perform(get("/prices/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$._embedded.prices[0].currency").value("USD"));
    }

    @Test
    public void getPriceById() throws Exception {
        mockMvc.perform(get("/prices/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.price").isNumber());
    }
}
