package io.klykov.moviecatalogservice.resources;

import io.klykov.moviecatalogservice.models.CatalogItem;
import io.klykov.moviecatalogservice.models.Movie;
import io.klykov.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogRersource {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = webClientBuilder.build()
                .get()
                .uri("http://rating-data-service/ratingsdata/user/" + userId)
                .retrieve()
                .bodyToMono(UserRating.class)
                .block();

        return ratings.getUserRating().stream().map(rating -> {
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://movie-info-service/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        }).collect(Collectors.toList());
    }
}

