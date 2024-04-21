import java.util.ArrayList;
import java.util.Random;

public class HW04_20220808006 {

}

class Computer {

    protected CPU cpu;
    protected RAM ram;

    public Computer(CPU cpu, RAM ram) {
        this.cpu = cpu;
        this.ram = ram;
    }

    public void run() throws ComputationException, MemoryException {

        try {
            int sum = 0;
            for (int i = 0; i < ram.getCapacity(); i++) {
                for (int j = 0; j < ram.getCapacity(); j++) {
                    if (i == j) {
                        sum += ram.getValue(i, j);
                    }
                }
            }
            ram.setValue(0, 0, cpu.compute(0, sum));
        } catch (ComputationException e) {
            e.fixComputation(0, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "Computer: " + cpu + " " + ram;
    }
}

class Laptop extends Computer {

    private int milliAmp;
    private int battery;

    public Laptop(CPU cpu, RAM ram, int milliAmp) {
        super(cpu, ram);
        this.milliAmp = milliAmp;
        this.battery = milliAmp * 3 / 10;
    }

    public int batteryPercentage() {
        return (battery * 100) / milliAmp;
    }

    public void charge() {
        while (battery < milliAmp * 9 / 10) {
            battery += milliAmp * 2 / 100;
        }
    }

    @Override
    public void run() throws ComputationException, MemoryException {
        if (batteryPercentage() > 5) {
            super.run();
            battery -= milliAmp * 3 / 100;
        } else {
            charge();
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + battery;
    }
}

class Desktop extends Computer {

    private ArrayList<String> peripherals = new ArrayList<>();

    public Desktop(CPU cpu, RAM ram, ArrayList<String> peripherals) {
        super(cpu, ram);
        this.peripherals = peripherals;
    }

    @Override
    public void run() throws ComputationException, MemoryException {
        int sum = 0;
        try {
            for (int i = 0; i < ram.getCapacity(); i++) {
                for (int j = 0; j < ram.getCapacity(); j++) {
                    sum = cpu.compute(0, ram.getValue(i, j));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ram.setValue(0, 0, sum);
    }

    public void plugIn(String peripheral) {
        peripherals.add(peripheral);
    }

    public String plugOut() {
        String str = peripherals.get(peripherals.size() - 1);
        peripherals.remove(peripherals.size() - 1);
        return str;
    }

    public String plugOut(int index) {
        String str = peripherals.get(index);
        peripherals.remove(index);
        return str;
    }

    @Override
    public String toString() {

        return super.toString() + " " + String.join(" ", peripherals);
    }
}

class CPU {

    private String name;
    private double clock;

    public CPU(String name, double clock) {
        this.name = name;
        this.clock = clock;
    }

    public double getClock() {
        return clock;
    }

    public String getName() {
        return name;
    }

    public int compute(int a, int b) throws InterruptedException,
            ComputationException {
        int sum = a + b;
        Thread.sleep((long) (5 / clock * 1000));
        if (sum < 0) {
            throw new ComputationException(this, a, b);
        }
        return sum;
    }

    @Override
    public String toString() {
        return "CPU: " + name + " " + clock + "Ghz";
    }
}

class RAM {

    private String type;
    private int capacity;
    private int[][] memory;

    public RAM(String type, int capacity) {
        this.type = type;
        this.capacity = capacity;
        initMemory();
    }

    private void initMemory() {

        Random rand = new Random();
        this.memory = new int[capacity][capacity];
        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                memory[i][j] = rand.nextInt(11);
            }
        }
    }

    private boolean check(int i, int j) throws MemoryException {

        if (i >= getCapacity() || j >= getCapacity() || i < 0 || j < 0) {
            throw new MemoryException(this, i, j);
        } else {
            return true;
        }
    }

    public int getValue(int i, int j) throws MemoryException {
        if (i >= getCapacity() || j >= getCapacity() || i < 0 || j < 0) {
            throw new MemoryException(this, i, j);
        } else {
            return memory[i][j];
        }
    }

    public void setValue(int i, int j, int value) {
        memory[i][j] = value;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "RAM: " + type + " " + capacity + "GB";
    }
}

class MemoryException extends Exception {

    private RAM ram;
    private int address1;
    private int address2;

    public MemoryException(RAM ram, int address1, int address2) {
        super("Memory out of range! With addresses #[" + address1 +
                ", " + address2 + "]");
        this.ram = ram;
        this.address1 = address1;
        this.address2 = address2;
    }
}

class ComputationException extends Exception {

    private CPU cpu;
    private int value1;
    private int value2;

    public ComputationException(CPU cpu, int value1, int value2) {
        super("Computation exception occured on " + cpu
                + " with values: "
                + "(" + value1 + ", " + value2 + ")");
        this.cpu = cpu;
        this.value1 = value1;
        this.value2 = value2;
    }

    public ComputationException(ComputationException e) {
        super("Unhandled exception occurred at " + e.cpu
                + " with values  "
                + e.value1 + " and " + e.value2 + ":\n\t"
                + e.getMessage());
    }

    public int fixComputation(int val1, int val2)
            throws ComputationException {

        try {
            int v1 = Math.abs(val1);
            int v2 = Math.abs(val2);
            int sum = cpu.compute(v1, v2);
            return sum;
        } catch (ComputationException e) {
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace(); //
            return -1;
        }
    }
}
