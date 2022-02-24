package info.kgeorgiy.ja.antonov.implementor.main;

public class PrimitiveDefaults {
    // These gets initialized to their default values
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
    private static char DEFAULT_CHAR;

    public static String getDefaultValue(Class<?> clazz) {
        if (clazz.equals(boolean.class)) {
            return Boolean.toString(DEFAULT_BOOLEAN);
        } else if (clazz.equals(byte.class)) {
            return Byte.toString(DEFAULT_BYTE);
        } else if (clazz.equals(short.class)) {
            return DEFAULT_SHORT + "";
        } else if (clazz.equals(int.class)) {
            return DEFAULT_INT + "";
        } else if (clazz.equals(long.class)) {
            return DEFAULT_LONG + "L";
        } else if (clazz.equals(float.class)) {
            return DEFAULT_FLOAT + "f";
        } else if (clazz.equals(double.class)) {
            return DEFAULT_DOUBLE + "";
        } else if (clazz.equals(char.class)) {
            return "'" + DEFAULT_CHAR + "'";
        }

        else {
            throw new IllegalArgumentException(
                    "Class type " + clazz + " not supported");
        }
    }
}