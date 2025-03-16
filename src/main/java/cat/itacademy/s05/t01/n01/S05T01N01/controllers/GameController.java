package cat.itacademy.s05.t01.n01.S05T01N01.controllers;

import cat.itacademy.s05.t01.n01.S05T01N01.models.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.services.GameService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;
    @PostMapping("/new")
    public Mono<ResponseEntity<Game>> createGame(
            @RequestParam String playerName) {
        return gameService.createGame(playerName)
                .map(game -> ResponseEntity.status(HttpStatus.CREATED).body(game));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Game>> getGame(@PathVariable String id) {
        return gameService.getGame(id)
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(game));
    }

    @PostMapping("/{id}/play")
    public Mono<ResponseEntity<Game>> playGame(@PathVariable String id) {
        return gameService.playGame(id)
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(game));
    }

    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id).thenReturn(ResponseEntity.noContent().build());
    }

    /*@GetMapping("/ranking")
    public Mono<ResponseEntity<Game>> getRanking() {
        return gameService.getRanking()
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(game));
    }*/
}
