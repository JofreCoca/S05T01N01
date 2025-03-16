package cat.itacademy.s05.t01.n01.S05T01N01.services;

import cat.itacademy.s05.t01.n01.S05T01N01.models.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private void executePlayer(Game game){
        while(game.getPlayercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum()<19){
            game.getPlayercards().add(game.getDeckcards().get(0));
            game.getDeckcards().remove(0);
        }
    }

    private void executeCroupier(Game game){
        while(game.getCroupiercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum()<17){
            game.getCroupiercards().add(game.getDeckcards().get(0));
            game.getDeckcards().remove(0);
        }
        game.setFinish(true);
    }
}
