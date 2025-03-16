package cat.itacademy.s05.t01.n01.S05T01N01;
import cat.itacademy.s05.t01.n01.S05T01N01.models.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.controllers.PlayerController;
import cat.itacademy.s05.t01.n01.S05T01N01.services.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Objects;
import static org.mockito.Mockito.when;

class PlayerControllerTest {
    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;
    private Player player1 = new Player("Player1");
    private Player player2 = new Player("Player2");

    @BeforeEach
    void initialSetup() {
        MockitoAnnotations.openMocks(this);
        player1.setId(1);
        player1.setScore(10);
        player2.setId(2);
        player2.setScore(20);
    }

    @Test
    void updatePlayerNameWhenFound() {
        String newName = "NameUpdate";
        Player expectedPlayer = new Player(newName);
        expectedPlayer.setId(player1.getId());
        expectedPlayer.setScore(player1.getScore());
        when(playerService.updatePlayerName(player1.getId(), newName)).thenReturn(Mono.just(expectedPlayer));
        StepVerifier.create(playerController.updatePlayerName(player1.getId(), newName))
                .expectNextMatches(response -> response.getStatusCode().equals(HttpStatus.OK) &&
                        Objects.equals(response.getBody(), expectedPlayer))
                .verifyComplete();
    }

    @Test
    void updatePlayerNameWhenNotFound() {
        String newName = "PlayerNoFound";
        int playerId = 10000;
        when(playerService.updatePlayerName(playerId, newName)).thenReturn(Mono.error(new RuntimeException("Player not found")));
        StepVerifier.create(playerController.updatePlayerName(playerId, newName))
                .expectError(RuntimeException.class)
                .verify();
    }
}



