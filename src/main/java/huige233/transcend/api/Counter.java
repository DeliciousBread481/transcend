package huige233.transcend.api;

public class Counter {
    public int value;

    public Counter(int value) {
        this.value = value;
    }

    public void decrement() {
        value--;
    }

    public void increment() {
        value++;
    }
}
