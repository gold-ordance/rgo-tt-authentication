package rgo.tt.authentication;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MainTest {

    private static final int PORT = 8082;
    private static final String HOST = "http://127.0.0.1:" + PORT;

    private static final String LIVENESS_URL = HOST + "/internal/liveness";
    private static final String READINESS_URL = HOST + "/internal/readiness";

    private final WebClient client = WebClient.of();

    @Test
    void livenessProbe() {
        AggregatedHttpResponse response = fetch(LIVENESS_URL);
        assertThat(response.status()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void readinessProbe() {
        AggregatedHttpResponse response = fetch(READINESS_URL);
        assertThat(response.status()).isEqualTo(HttpStatus.OK);
    }

    private AggregatedHttpResponse fetch(String url) {
        return client.get(url).aggregate().join();
    }
}
