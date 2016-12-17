package nameserver;

/**
 * Created by consti on 17.12.16.
 */
public class Tupel<T1,T2> {
    public T1 t1;
    public T2 t2;

    public Tupel() {
        t1 = null;
        t2 = null;
    }
    public Tupel(T1 first, T2 second) {
        this.t1 = first;
        this.t2 = second;
    }
    public String toString() {
        if (t1 == null || t2 == null) {
            return null;
        } else {
            return t1.toString() + " - " + t2.toString();
        }
    }

}
