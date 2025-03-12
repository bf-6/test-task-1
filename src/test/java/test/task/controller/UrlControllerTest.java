package test.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import test.task.mapper.UrlMapper;
import test.task.model.Url;
import test.task.repository.UrlRepository;
import test.task.service.UrlService;
import test.task.util.ModelGenerator;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlMapper urlMapper;

    @Mock
    private UrlService urlService;

    @Autowired
    private ModelGenerator modelGenerator;

    private Url testUrl;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        testUrl = Instancio.of(modelGenerator.getUrlModel()).create();
        urlRepository.save(testUrl);
    }

    @AfterEach
    void tearDown() {
        urlRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUrlModel())
                .create();

        var request = post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var url = urlRepository.findByUrl(data.getUrl()).orElse(null);
        data.setShortId(url.getShortId());

        assertNotNull(url);
        assertThat(url.getUrl()).isEqualTo(data.getUrl());
        assertThat(url.getShortId()).isEqualTo(data.getShortId());
    }

    @Test
    void testUpdate() throws Exception {
        var url = "new-url";

        var data = new HashMap<>();
        data.put("shortId", url);

        var request = put("/api/urls/" + testUrl.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var name = urlRepository.findByUrl(testUrl.getUrl()).orElse(null);
        assertThat(name.getShortId()).isEqualTo(url);
    }

    @Test
    void testShow() throws Exception {
        var data = Instancio.of(modelGenerator.getUrlModel()).create();
        var shortId = faker.regexify("[a-zA-Z0-9]{8}");
        data.setShortId(shortId);

        urlRepository.save(data);

        var request = get("/api/urls/" + data.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("url").isEqualTo(data.getUrl()),
                v -> v.node("shortId").isEqualTo(data.getShortId())
        );
    }

    @Test
    void redirectWhenShortUrlExists() throws Exception {
        var data = Instancio.of(modelGenerator.getUrlModel()).create();
        var shortId = faker.regexify("[a-zA-Z0-9]{8}");
        var originalUrl = "https://example.com";
        data.setUrl(originalUrl);
        data.setShortId(shortId);

        urlRepository.save(data);

        mockMvc.perform(get("/api/{shortId}", shortId))
                .andExpect(status().isFound()) // 302 Found
                .andExpect(header().string("Location", originalUrl));

    }

    @Test
    void returnNotFoundWhenShortUrlMissing() throws Exception {
        var shortId = faker.regexify("[a-zA-Z0-9]{8}");
        Mockito.when(urlService.getOriginalUrl(shortId)).thenReturn(null);

        mockMvc.perform(get("/api/{shortId}", shortId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/urls/" + testUrl.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var data = urlRepository.findById(testUrl.getId()).orElse(null);
        assertThat(data).isNull();
    }
}
