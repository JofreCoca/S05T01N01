package cat.itacademy.s05.t01.n01.S05T01N01.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.lang.annotation.Documented;
import java.util.ArrayList;

@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private String namePlayer;
    private ArrayList<Card> Deckcards;
    private ArrayList<Card> Croupiercards;
    private ArrayList<Card> Playercards;
    private boolean finish;
    private int score;

    public Game(String namePlayer) {
        this.namePlayer = namePlayer;
        this.Deckcards = new Deck().getCards();
        this.Croupiercards = new ArrayList<>();
        this.Playercards = new ArrayList<>();
        this.finish = false;
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public ArrayList<Card> getDeckcards() {
        return Deckcards;
    }

    public void setDeckcards(ArrayList<Card> deckcards) {
        Deckcards = deckcards;
    }

    public ArrayList<Card> getCroupiercards() {
        return Croupiercards;
    }

    public void setCroupiercards(ArrayList<Card> croupiercards) {
        Croupiercards = croupiercards;
    }

    public ArrayList<Card> getPlayercards() {
        return Playercards;
    }

    public void setPlayercards(ArrayList<Card> playercards) {
        Playercards = playercards;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFinish() {
        return finish;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void initialDistribution(){




    }
}
