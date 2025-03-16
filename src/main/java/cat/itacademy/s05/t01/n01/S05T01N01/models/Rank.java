package cat.itacademy.s05.t01.n01.S05T01N01.models;

public enum Rank {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    ACE(1);

    private final int valor;

    Rank(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}

