package nachos.verify;

public class TestAlarmStatic {
    private static boolean Testing = false;

    public static boolean isTesting() {
        return Testing;
    }

    public static void setTesting(boolean testing) {
        Testing = testing;
    }
}
