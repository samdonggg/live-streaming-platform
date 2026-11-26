package com.livestream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApiApplicationTests {

    @Test
    void 애플리케이션_컨텍스트가_로드된다() {
    }

}
