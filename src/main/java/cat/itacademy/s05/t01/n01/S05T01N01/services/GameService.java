package cat.itacademy.s05.t01.n01.S05T01N01.services;

import cat.itacademy.s05.t01.n01.S05T01N01.models.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GameService {
    @Autowired
    private GameRepository repository;

    public Mono<Game> createGame(String namePlayer) {
        Game game = new Game(namePlayer);
        return repository.save(game);
    }

    public Mono<Game> getGame(String id) {
        return repository.findById(id);
    }

    public Mono<Game> playGame(String id) {
        return repository.findById(id)
                .flatMap(game -> {
                    if (game.isFinish()) {
                        return Mono.just(game);
                    }
                    executeRoll(game);
                    executePlayer(game);
                    executeCroupier(game);
                    finishGame(game);
                    return repository.save(game)
                            .flatMap(savedGame -> repository.findById(savedGame.getId())); // por si quieres "refrescar"
                });
    }

    public Mono<Void> deleteGame(String id){
        return repository.deleteById(id);
    }

    private void executeRoll(Game game){
        game.getCroupiercards().add(game.getDeckcards().get(0));
        game.getDeckcards().remove(0);
        game.getCroupiercards().add(game.getDeckcards().get(0));
        game.getDeckcards().remove(0);

        game.getPlayercards().add(game.getDeckcards().get(0));
        game.getDeckcards().remove(0);
        game.getPlayercards().add(game.getDeckcards().get(0));
        game.getDeckcards().remove(0);
    }

    public Mono<Game> executePlayer(Game game) {
        return Flux.defer(() -> Flux.fromIterable(game.getPlayercards()))
                .map(card -> card.getRank().getValor())
                .reduce(Integer::sum)
                .flatMapMany(total -> {
                    if (total >= 19) {
                        return Flux.just(game); // no hace falta más
                    } else {
                        return Mono.fromCallable(() -> {
                            game.getPlayercards().add(game.getDeckcards().get(0));
                            game.getDeckcards().remove(0);
                            return game;
                        }).flatMapMany(updatedGame -> executePlayer(updatedGame).flux()); // recursivo
                    }
                })
                .last(); // obtenemos el último estado del game
    }


    public Mono<Game> executeCroupier(Game game) {
        return Flux.defer(() -> Flux.fromIterable(game.getCroupiercards()))
                .map(card -> card.getRank().getValor())
                .reduce(Integer::sum)
                .flatMapMany(total -> {
                    if (total >= 17) {
                        return Flux.just(game); // el crupier se planta
                    } else {
                        return Mono.fromCallable(() -> {
                            game.getCroupiercards().add(game.getDeckcards().get(0));
                            game.getDeckcards().remove(0);
                            return game;
                        }).flatMapMany(updatedGame -> executeCroupier(updatedGame).flux()); // recursivo
                    }
                })
                .last(); // obtenemos el último estado del game
    }


    public Mono<Game> finishGame(Game game) {
        Mono<Integer> puntuacionPlayerMono = Flux.fromIterable(game.getPlayercards())
                .map(card -> card.getRank().getValor())
                .reduce(Integer::sum);

        Mono<Integer> puntuacionCroupierMono = Flux.fromIterable(game.getCroupiercards())
                .map(card -> card.getRank().getValor())
                .reduce(Integer::sum);

        return Mono.zip(puntuacionPlayerMono, puntuacionCroupierMono)
                .map(tuple -> {
                    int puntuacionPlayer = tuple.getT1();
                    int puntuacionCroupier = tuple.getT2();

                    if ((puntuacionPlayer > puntuacionCroupier) && puntuacionPlayer < 22) {
                        game.setScore(10);
                    }
                    game.setFinish(true);
                    return game;
                });
    }

    public Mono<Game> getRanking() {
        return repository.findAll()
                .filter(Game::isFinish)
                .sort((g1, g2) -> Integer.compare(g2.getScore(), g1.getScore()))
                .next();
    }

}
