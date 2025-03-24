package cat.itacademy.s05.t01.n01.S05T01N01.services;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository repository;
    private PlayerService playerService;

    public GameService(GameRepository repository, PlayerService playerService) {
        this.repository = repository;
        this.playerService = playerService;
    }

    public Mono<Game> createGame(String namePlayer) {
        Game game = new Game(namePlayer);
        executeRoll(game);
        return playerService.findByName(namePlayer)
                .switchIfEmpty(playerService.add(new Player(namePlayer)))
                .flatMap(player -> {
                    return repository.save(game);
                });
    }

    public Mono<Game> getGame(String id) {
        return repository.findById(id);
    }

    public Mono<Game> playGame(String id, String nextMove) {
        return repository.findById(id)
                .flatMap(game -> {
                    if (game.isFinish()) {
                        return Mono.just(game);
                    }
                    executePlayer(game, nextMove);
                    return repository.save(game)
                            .flatMap(savedGame -> repository.findById(savedGame.getId()));
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

    public Mono<Game> executePlayer(Game game, String nextMove) {
        if(nextMove.equalsIgnoreCase("hit")){
            game.getPlayercards().add(game.getDeckcards().get(0));
            game.getDeckcards().remove(0);
            if(game.getPlayercards().stream()
                    .mapToInt(card -> card.getRank().getValor())
                    .sum()>21){
                finishGame(game);
            }
        }
        if(nextMove.equalsIgnoreCase("stand")){
            executeCroupier(game);
            finishGame(game);
        }

        return repository.save(game);
    }

    public void executeCroupier(Game game) {
        while(game.getCroupiercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum()<17){
            game.getCroupiercards().add(game.getDeckcards().get(0));
            game.getDeckcards().remove(0);
        }
    }

    public void finishGame(Game game){
        if((game.getPlayercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum()<22) && (game.getCroupiercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum() < game.getPlayercards().stream()
                .mapToInt(card -> card.getRank().getValor())
                .sum())){
            game.setScore(10);

            playerService.findByName(game.getNamePlayer())
                    .flatMap(player -> {
                        player.setScore(player.getScore() + 10);
                        return playerService.update(player);
                    })
                    .subscribe();
        }
        game.setFinish(true);
    }

    public Mono<Player> getRanking() {
        return playerService.getAll()
                .sort((g1, g2) -> Double.compare(g2.getScore(), g1.getScore()))
                .next();
    }
}
