package sj.messenger.domain.groupchat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import sj.messenger.util.integration.EnableContainers

@SpringBootTest
@EnableContainers
class ChatControllerTest(
    @Autowired val mockMvc : MockMvc,
){

}


