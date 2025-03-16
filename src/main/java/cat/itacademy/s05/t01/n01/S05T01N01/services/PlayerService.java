package cat.itacademy.s05.t01.n01.S05T01N01.services;

import cat.itacademy.s05.t01.n01.S05T01N01.exceptions.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository repository;

    public Mono<Player> add(Player player) {
        return repository.save(player);
    }

    public Mono<Player> updatePlayerName(int id, String newName) {
        if (newName == null) {
            return Mono.error(new IllegalArgumentException("Name cannot be null"));
        }
        return repository.findById(id)
                .flatMap(p -> {
                    p.setName(newName);
                    return repository.save(p);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Player not found")));
    }

    public Mono<Player> update(Player player) {
        if (player.getId() == 0) {
            return Mono.error(new PlayerNotFoundException("The id is null or invalid"));
        }
        return repository.existsById(player.getId())
                .flatMap(exists -> {
                    if (exists) {
                        return repository.save(player);
                    } else {
                        return Mono.error(new PlayerNotFoundException("The player does not exist"));
                    }
                });
    }

    public Mono<Void> delete(int id) {
        if (id == 0) {
            return Mono.error(new PlayerNotFoundException("The id is null or invalid"));
        }

        return repository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return repository.deleteById(id);
                    } else {
                        return Mono.error(new PlayerNotFoundException("The player does not exist"));
                    }
                });
    }

    public Mono<Player> getOne(int id) {
        if (id == 0) {
            return Mono.error(new IllegalArgumentException("The id is null or invalid"));
        }
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The player does not exist")));
    }

    public Flux<Player> getAll() {
        return repository.findAll();
    }

    public Flux<Player> getPlayersSorted() {
        return this.repository.findAll()
                .sort(Comparator.comparingDouble(Player::getScore).reversed())
                .switchIfEmpty(Mono.empty());
    }
}
