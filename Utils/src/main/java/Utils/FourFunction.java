package Utils;


@FunctionalInterface
public interface  FourFunction<A,B,C,D,R> {
    R apply(A item1, B item2, C item3, D item4);
}