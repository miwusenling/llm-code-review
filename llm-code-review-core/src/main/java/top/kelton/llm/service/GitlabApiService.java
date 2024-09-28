package top.kelton.llm.service;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.interfaces.Payload;

public class GitlabApiService {

    @PostMapping("/webhook-endpoint")
    public String handleWebhook(@RequestBody String payload){
     
        PayloadDTO object = JSON.parseObject(payload, PayloadDTO.class);  // PayloadDTO

        // call llm

        // call gitlab api


        return "webhook received and processed";
    }

    
    
}
