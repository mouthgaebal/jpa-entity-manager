package jdbc;

@FunctionalInterface
public interface FunctionWithException<T, R> {
    R apply(T t) throws Exception;
}
