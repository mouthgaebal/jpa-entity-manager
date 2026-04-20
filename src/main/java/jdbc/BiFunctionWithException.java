package jdbc;

public interface BiFunctionWithException<T, U, R> {

    R apply(T var1, U var2) throws Exception;

}
