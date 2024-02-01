package huige233.transcend.api;

public class Tuple<K, V> {

    public final K key;
    public final V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
////        hellfirepvp.astralsorcery.common.util.data.Tuple tuple = (hellfirepvp.astralsorcery.common.util.data.Tuple) o;
////        return (key == null ? tuple.key == null : key.equals(tuple.key)) && (value == null ? tuple.value == null : value.equals(tuple.value));
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (getKey() != null ? !getKey().equals(tuple.getKey()) : tuple.getKey() != null) return false;
        return getValue() != null ? getValue().equals(tuple.getValue()) : tuple.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
