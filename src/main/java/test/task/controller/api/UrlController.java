package test.task.controller.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import test.task.dto.UrlCreateDTO;
import test.task.dto.UrlDTO;
import test.task.dto.UrlUpdateDTO;
import test.task.service.UrlService;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/urls")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDTO create(@RequestBody @Valid UrlCreateDTO urlDTO) {
        return urlService.create(urlDTO);
    }

    @GetMapping("/urls/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UrlDTO show(@PathVariable Long id) {
        return urlService.show(id);
    }

    @PutMapping("/urls/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UrlDTO update(@RequestBody @Valid UrlUpdateDTO urlDTO, @PathVariable Long id) {
        return urlService.update(urlDTO, id);
    }


    @GetMapping("/{shortId}")
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    public ResponseEntity<Object> redirectToUrl(@PathVariable String shortId) {
        return urlService.getOriginalUrl(shortId)
                .map(url -> ResponseEntity.status(302).location(URI.create(url)).build())
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/urls/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        urlService.destroy(id);
    }
}
