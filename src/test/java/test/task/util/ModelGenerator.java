package test.task.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.task.model.Url;

@Getter
@Component
public class ModelGenerator {

    private Model<Url> urlModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        urlModel = Instancio.of(Url.class)
                .ignore(Select.field(Url::getId))
                .ignore(Select.field(Url::getCreatedAt))
                .ignore(Select.field(Url::getShortId))
                .supply(Select.field(Url::getUrl), () -> faker.internet().url())
                .toModel();
    }
}
