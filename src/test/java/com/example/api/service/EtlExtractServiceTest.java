package com.example.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
public class EtlExtractServiceTest {

    @Resource
    private EtlExtractService etlExtractService;
    @Test
    void test(){
        etlExtractService.executorClient("wenku-book",
                new HashMap<>(0),new HashMap<>(0));
    }
}
