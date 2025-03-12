package test.task.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import test.task.dto.UrlCreateDTO;
import test.task.dto.UrlDTO;
import test.task.dto.UrlUpdateDTO;
import test.task.exception.ResourceNotFoundException;
import test.task.mapper.UrlMapper;
import test.task.model.Url;
import test.task.repository.UrlRepository;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlDTO create(UrlCreateDTO urlDTO) {
        var url = urlMapper.map(urlDTO);

        var shortId = UUID.randomUUID().toString().substring(0, 8);
        url.setShortId(shortId);

        urlRepository.save(url);
        return urlMapper.map(url);
    }

    public UrlDTO show(Long id) {
        return urlMapper.map(urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Url with id " + id + " not found!")));
    }

    public UrlDTO update(UrlUpdateDTO urlDTO, Long id) {
        var url = urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Url with id " + id + " not fount!"));
        urlMapper.update(urlDTO, url);
        urlRepository.save(url);
        return urlMapper.map(url);
    }

    public void destroy(Long id) {
        urlRepository.deleteById(id);
    }

    public Optional<String> getOriginalUrl(String shortId) {
        return urlRepository.findByShortId(shortId).map(Url::getUrl);
    }
}
