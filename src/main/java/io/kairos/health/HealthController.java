package io.kairos.health;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthController {

    record HealthDetail(String source, Integer result) {}

    private final JdbcClient jdbcClient;

    public HealthController(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @GetMapping("/health")
    public List<HealthDetail> health() {
        Integer result = jdbcClient.sql("SELECT 1")
                .query(Integer.class)
                .single();

        return List.of(
            new HealthDetail(
                "application",
                1
            ),
            new HealthDetail(
                "database",
                result != null && result == 1 ? 1 : 0
            )
        );
    }
}
