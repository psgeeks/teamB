package 광물운송;

import java.util.Scanner;

class Solution {

    private final static int CMD_INIT = 1;
    private final static int CMD_ADD = 2;
    private final static int CMD_DROP = 3;

    private final static UserSolution usersolution = new UserSolution();

    private static boolean run(Scanner sc) throws Exception {

        int query_num = sc.nextInt();
        boolean ok = false;

        for (int q = 0; q < query_num; q++) {
            int query = sc.nextInt();

            if (query == CMD_INIT) {
                int L = sc.nextInt();
                int N = sc.nextInt();
                usersolution.init(L, N);
                ok = true;
            } else if (query == CMD_ADD) {
                int mID = sc.nextInt();
                int mRow = sc.nextInt();
                int mCol = sc.nextInt();
                int mQuantity = sc.nextInt();
                int ret = usersolution.addBaseCamp(mID, mRow, mCol, mQuantity);
                int ans = sc.nextInt();
                if (ans != ret) {
                    ok = false;
                }
            } else if (query == CMD_DROP) {
                int K = sc.nextInt();
                int ret = usersolution.findBaseCampForDropping(K);
                int ans = sc.nextInt();
                if (ans != ret) {
                    ok = false;
                }
            }
        }
        return ok;
    }

    public static void main(String[] args) throws Exception {
        int T, MARK;
        // System.setIn(new java.io.FileInputStream("res/sample_input.txt"));

        Scanner sc = new Scanner(System.in);        

        T = sc.nextInt();
        MARK = sc.nextInt();

        for (int tc = 1; tc <= T; tc++) {
            int score = run(sc) ? MARK : 0;
            System.out.println("#" + tc + " " + score);
        }
        sc.close();
    }
}