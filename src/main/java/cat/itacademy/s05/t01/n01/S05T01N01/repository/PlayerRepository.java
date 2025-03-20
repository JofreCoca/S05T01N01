package cat.itacademy.s05.t01.n01.S05T01N01.repository;

import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Integer> {
    Mono<Player> findByName(String name);
}
