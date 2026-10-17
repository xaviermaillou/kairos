package io.kairos.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthController {

    record HealthDetail(String source, Integer result) {}

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public List<HealthDetail> health() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

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
