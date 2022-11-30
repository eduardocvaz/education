package com.labcomu.edu.client;

import com.labcomu.edu.configuration.EduProperties;
import com.labcomu.edu.resource.Researcher;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotNull;

@Component
@Validated
public class OrcidGateway {
    private final String fetchResearcherUrl;

    private final WebClient.Builder webClientBuilder;

    public OrcidGateway(final WebClient.Builder webClientBuilder,
            final EduProperties properties) {
        this.webClientBuilder = webClientBuilder;
        this.fetchResearcherUrl = properties.getUrl().getFetchResearcherDetails();

    }
//    @Retry(name = "flightSearch")
    public Researcher getResearcher(@NotNull final String orcid) {
        return webClientBuilder.build()
                .get()
                .uri(fetchResearcherUrl, orcid)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Researcher.class)
                .block();
    }
}
