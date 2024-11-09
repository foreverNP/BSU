package restaurant.builders;

public abstract class BaseBuilder<T> {
    protected T object;

    public T getObject() {
        return object;
    }

    public abstract void build();
}
