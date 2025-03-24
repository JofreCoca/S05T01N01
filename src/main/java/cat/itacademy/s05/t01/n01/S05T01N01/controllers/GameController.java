package cat.itacademy.s05.t01.n01.S05T01N01.controllers;

import cat.itacademy.s05.t01.n01.S05T01N01.models.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.services.GameService;
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
    public Mono<ResponseEntity<Game>> playGame(@RequestParam String id, @RequestParam String nextMove) {
        return gameService.playGame(id, nextMove)
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(game));
    }

    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id).thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/ranking")
    public Mono<ResponseEntity<Player>> getRanking() {
        return gameService.getRanking()
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(game));
    }
}
