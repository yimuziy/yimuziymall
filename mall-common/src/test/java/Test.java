/**
 * @author ywz
 * @date 2020/12/9 15:42
 * @description
 */
public class Test {

    public static String output = "";

    public static void foo(int i) {
        try {
            if (i == 1) {
                throw new Exception();
            }
            output += "1";
        } catch (Exception e) {
            output += "2";
            return;
        } finally {
            output += "3";
        }
        output += "3";
    }

    public static void main(String[] args) {
        foo(0);
        System.out.println(output);
        foo(1);
        System.out.println(output);


        int i = 0, j = 5;
        tp:
        for (; ; i++) {

            for (; ; j--) {
                if (i > j)
                    break tp;

            }
        }

        System.out.println("i=" + i + ",j=" + j);
    }
}
