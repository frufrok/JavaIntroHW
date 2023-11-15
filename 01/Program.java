import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        // String equation = "2? + ?5 = 69";
        String equation = "? + 1? = ??";
        //System.out.println("Введите выражение вида q + w = e, где q, w, e >= 0. Вы можете заменить любую цифру в числах знаком. Мы попробуем восстановить значения.");
        //Scanner console = new Scanner(System.in);
        //String equation = console.nextLine();
        //console.close();
        //System.out.println(restoreEquation(equation));
        restoreEquation(equation);
    }

    static void restoreEquation(String equation) {
        Counter counter = new Counter();
        counter.count = 0;
        restoreEquationRecursion(equation, true, counter);
        if (counter.count<1)
        throw new IllegalArgumentException(
                "Выражение " + equation + " не может быть восстановлено.");
        else
            System.out.printf("Получено решений: %d.", counter.count);
    }

    static void restoreEquationRecursion(String equation, boolean parent, Counter counter) {
        IllegalArgumentException typeError = new IllegalArgumentException(
                "Выражение " + equation + " не соответствует виду q + w = e.");
        IllegalArgumentException logicalError = new IllegalArgumentException(
                "Выражение " + equation + " не может быть восстановлено.");
        String[] parts = equation.replaceAll(" ", "").split("=");
        if (parts.length != 2)
            throw typeError;
        String[] leftParts = parts[0].split("\\+");
        if (leftParts.length != 2)
            throw typeError;
        int digits = Math.max(Math.max(leftParts[0].length(), leftParts[1].length()), parts[1].length());
        int[] q = stringToIntArray(leftParts[0], digits);
        int[] w = stringToIntArray(leftParts[1], digits);
        int[] e = stringToIntArray(parts[1], digits);

        boolean valid = true;
        for (int i = digits - 1; i >= 0; i--) {
            if (q[i] + w[i] + e[i] == -3) {
                valid = false;
                int min = (i == 0 && digits>1)? 1 : 0;
                for (int j = min; j < 10; j++) {
                    int[] variant = replaceAt(e, i, j);
                    restoreEquationRecursion(
                            String.format("%s + %s = %s", intArrayToString(q), intArrayToString(w),
                                    intArrayToString(variant)),
                            false, counter);

                }
            }
            else if (q[i] + w[i] == -2 || q[i] + e[i] == -2 || w[i] + e[i] == -2) {
                valid = false;
                int min = (i == 0 && digits>1) ? 1 : 0;
                if (q[i] + w[i] == -2)
                    for (int j = min; j < 10; j++) {
                        int[] variant = replaceAt(q, i, j);
                        restoreEquationRecursion(
                                String.format("%s + %s = %s", intArrayToString(variant), intArrayToString(w),
                                        intArrayToString(e)),
                                false, counter);
                    }
                if (q[i] + e[i] == -2)
                    for (int j = min; j < 10; j++) {
                        int[] variant = replaceAt(q, i, j);
                        restoreEquationRecursion(
                                String.format("%s + %s = %s", intArrayToString(variant), intArrayToString(w),
                                        intArrayToString(e)),
                                false, counter);
                    }
                if (w[i] + e[i] == -2)
                    for (int j = min; j < 10; j++) {
                        int[] variant = replaceAt(w, i, j);
                        restoreEquationRecursion(
                                String.format("%s + %s = %s", intArrayToString(q), intArrayToString(variant),
                                        intArrayToString(e)),
                                false, counter);
                    }
            }
        }
        if (valid) {
            int memory = 0;
            for (int i = digits - 1; i >= 0; i--) {
                if (q[i] == -1)
                    q[i] = e[i] + memory - w[i];
                if (w[i] == -1)
                    w[i] = e[i] + memory - q[i];
                if (e[i] == -1) {
                    e[i] = q[i] - memory + w[i];
                }
                if (q[i] + w[i] != e[i] + memory) {
                    if (parent)
                        throw logicalError;
                    else
                        return;
                }
                else
                    memory = 0;

                if (q[i] < 0) {
                    q[i] += 10;
                    memory = -1;
                }
                if (w[i] < 0) {
                    w[i] += 10;
                    memory = -1;
                }
                if (e[i] > 9) {
                    e[i] -= 10;
                    memory = -1;
                }
            }
            if (memory != 0) {
                if (parent)
                    throw logicalError;
                else
                    return;
            }
            String qs = intArrayToString(q);
            String ws = intArrayToString(w);
            String es = intArrayToString(e);
            if (qs.length() == leftParts[0].length() && ws.length() == leftParts[1].length()
                    && es.length() == parts[1].length())
            {
                System.out.println(
                    String.format("%s + %s = %s", intArrayToString(q), intArrayToString(w), intArrayToString(e)));
                    counter.count++;
            }
        }
    }

    static int[] stringToIntArray(String s, int digits) {
        int[] result = new int[digits];
        int minIndex = Math.max(0, digits - s.length());
        for (int i = digits - 1; i >= minIndex; i--) {
            try {
                result[i] = Integer.parseInt(String.valueOf(s.charAt(i - minIndex)));
            } catch (Exception e) {
                result[i] = -1;
            }
        }
        return result;
    }

    static String intArrayToString(int[] array) {
        String result = "";
        int length = array.length;
        Boolean started = false;
        for (int i = 0; i < length; i++) {
            if (array[i] != 0)
                started = true;
            if (started) {
                if (array[i] >= 0)
                    result += String.valueOf(array[i]);
                else
                    result += "?";
            }
        }
        return result == "" ? "0" : result;
    }

    static int[] replaceAt(int[] array, int index, int value) {
        int count = array.length;
        int[] result = new int[count];
        if (0 <= index && index < count) {
            for (int i = 0; i < index; i++)
                result[i] = array[i];
            result[index] = value;
            for (int i = index + 1; i < count; i++)
                result[i] = array[i];
            return result;
        } else {
            for (int i = 0; i < count; i++)
                result[i] = array[i];
            return result;
        }
    }
}

class Counter {
    int count;
}
