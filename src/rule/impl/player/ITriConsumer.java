package rule.impl.player;

public interface ITriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
