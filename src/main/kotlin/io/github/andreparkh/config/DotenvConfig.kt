package io.github.andreparkh.config

import io.github.cdimascio.dotenv.dotenv
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class DotenvConfig {
    @PostConstruct
    fun loadDotenv() {
        dotenv {
            ignoreIfMissing = true
        }
    }
}