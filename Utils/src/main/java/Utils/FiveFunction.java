package Utils;

@FunctionalInterface
public interface  FiveFunction<A,B,C,D,E,R> {
    R apply(A item1, B item2, C item3, D item4, E item5);
}