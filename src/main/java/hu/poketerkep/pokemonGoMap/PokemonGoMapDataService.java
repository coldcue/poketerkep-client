package hu.poketerkep.pokemonGoMap;

import hu.poketerkep.json.RawDataJsonDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PokemonGoMapDataService {

    public RawDataJsonDto getRawData() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:5000/raw_data?pokemon=true&pokestops=true&gyms=true&scanned=true", RawDataJsonDto.class);
    }

}
