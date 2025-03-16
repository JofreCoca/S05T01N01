package cat.itacademy.s05.t01.n01.S05T01N01.controllers;

import cat.itacademy.s05.t01.n01.S05T01N01.exceptions.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.services.PlayerService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerService service;

    @PostMapping("/add")
    public ResponseEntity<Mono<Player>> add(@RequestBody Player player) {
        return ResponseEntity.ok(service.add(player));
    }

    @PutMapping(value = {"/player/{playerId}"})
    public Mono<ResponseEntity<Player>> updatePlayerName(@Parameter(description = "Enter player id") @PathVariable Integer playerId,
                                                         @RequestBody @Schema(description = "Enter new player name") String newName){
        return service.updatePlayerName(playerId, newName).map(player ->
                ResponseEntity.status(HttpStatus.OK).body(player));
    }

    @PutMapping("/update")
    public ResponseEntity<Mono<Player>> update(@RequestBody Player player) {
        return ResponseEntity.ok(service.update(player));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable int id) {
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()))  // Respondemos con HTTP 204 No Content si todo salió bien
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build())); // Si hay un error, respondemos con HTTP 404 Not Found
    }

    @GetMapping("/getOne/{id}")
    public Mono<ResponseEntity<Player>> getOne(@PathVariable int id) {
        return service.getOne(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof PlayerNotFoundException || e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.error(e); // Propagar otros errores
                });
    }

    @GetMapping("/getAll")
    public ResponseEntity<Flux<Player>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(value = {"/ranking"})
    public Mono<ResponseEntity<List<Player>>> getPlayersRanking(){
        return service.getPlayersSorted().collectList().map(players ->
                ResponseEntity.status(HttpStatus.OK).body(players));
    }
}
