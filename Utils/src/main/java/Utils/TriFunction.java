package Utils;

@FunctionalInterface
public interface  TriFunction<A,B,C,R> {
    R apply(A item1, B item2, C item3);
}
