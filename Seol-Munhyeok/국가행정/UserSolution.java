package 국가행정;
import java.util.Arrays;
import java.util.TreeSet;
 
class UserSolution {
    final int INF = 2_000_000_000;
    final int MAX_N = 10000;
    int N;  // 도시 개수
    int bridgeN; // 다리 개수 (= N - 1)
    int[] population = new int[MAX_N + 1]; // 1-based로 저장
    int[] initDist = new int[MAX_N + 1];   // initDist[i] : i와 i + 1 도시 이동의 초기 시간 저장
    int[] bridgeCnt = new int[MAX_N + 1];  // bridgeCnt[i] : i와 i + 1 도시 사이 다리 개수 저장
    int[] sumTree = new int[4 * MAX_N];
    TreeSet<Node> ts = new TreeSet<>();
     
    static class Node implements Comparable<Node> {
        int max;
        int idx;
        Node (int max, int idx) {
            this.max = max;
            this.idx = idx;
        }
        @Override
        public int compareTo(Node o) {
            if (this.max != o.max) return Integer.compare(o.max, this.max);  // 값 내림차순
            return Integer.compare(this.idx, o.idx);  // 인덱스 오름차순
        }
    }
    
    private int initSumTree(int node, int start, int end) {
        if (start == end) return sumTree[node] = initDist[start];
         
        int mid = (start + end) / 2;
        return sumTree[node] = initSumTree(node * 2, start, mid)
                            + initSumTree(node * 2 + 1, mid + 1, end);
    }
     
    private int querySum(int node, int nodeL, int nodeR, int queryL, int queryR) {
        // 완전 미포함
        if (nodeR < queryL || queryR < nodeL) return 0;
         
        // 완전 포함
        if (queryL <= nodeL && nodeR <= queryR) return sumTree[node];
         
        // 부분 겹침
        int mid = (nodeL + nodeR) / 2;
        return querySum(node * 2, nodeL, mid, queryL, queryR) + 
               querySum(node * 2 + 1, mid + 1, nodeR, queryL, queryR);
    }
     
    private void updateSumTree(int node, int nodeL, int nodeR, int idx, int diff) {
        // idx가 이 구간에 없으면 종료
        if (idx < nodeL || nodeR < idx) return;
         
        // 현재 구간은 영향을 받음
        sumTree[node] += diff;
         
        // 리프가 아니면 계속 내려감
        if (nodeL != nodeR) {
            int mid = (nodeL + nodeR) / 2;
            updateSumTree(node * 2, nodeL, mid, idx, diff);
            updateSumTree(node * 2 + 1, mid + 1, nodeR, idx, diff);
        }
    }
     
     
    void init(int N, int mPopulation[]) {
        ts.clear();
         
        this.N = N;
        bridgeN = N - 1;
         
        for (int i = 1; i <= N; i++) {
            population[i] = mPopulation[i - 1];  // 주의 : population은 1-based
        }
         
        for (int i = 1; i < N; i++) {
            initDist[i] = (population[i] + population[i + 1]);
            ts.add(new Node(initDist[i], i));
        }
         
        Arrays.fill(bridgeCnt, 1);
         
        initSumTree(1, 1, bridgeN);
         
        return;
    }
 
    int expand(int M) {
        int changedDist = 0;
        for (int i = 0; i < M; i++) {
            Node maxNode = ts.pollFirst();
            bridgeCnt[maxNode.idx]++;  // 다리 추가 건설
             
            changedDist = initDist[maxNode.idx] / bridgeCnt[maxNode.idx];
            int diff = changedDist - maxNode.max;   // diff는 음수로
             
            updateSumTree(1, 1, bridgeN, maxNode.idx, diff);
            ts.add(new Node(changedDist, maxNode.idx));
        }
        return changedDist;
    }
     
    int calculate(int mFrom, int mTo) {
        // 대소관계 보장
        if (mFrom > mTo) {
            int t = mFrom;
            mFrom = mTo;
            mTo = t;
        }
        // 1-index로 보정
        mFrom++;
        mTo++;
        // 도시 1 - 3이면 필요한 다리는 (1-2), (2-3) 이므로 (1, 2) 다리의 합을 구한다.
        return querySum(1, 1, bridgeN, mFrom, mTo - 1);  
    }
     
    // 0-based로 판정함
    private boolean canDivide(int mFrom, int mTo, int limit, int K) {
        int cnt = 1;
        int sum = 0;
         
        for (int i = mFrom + 1; i <= mTo + 1; i++) {
            if (sum + population[i] <= limit) {
                sum += population[i];
            } else {
                cnt++;
                sum = population[i];
            }
        }
         
        // 필요한 선거구 개수가 K 이하이면 가능
        // (K보다 적게 나눌 수 있어도, 더 쪼개서 정확히 K개 만들 수 있음)
        return cnt <= K;
    }
     
    // 파라미터 0-based
    int divide(int mFrom, int mTo, int K) {
        int lo = 0;
        int hi = 0;
         
        // population은 1-based, 도시 ID는 0-based
        for (int i = mFrom + 1; i <= mTo + 1; i++) {
            lo = Math.max(lo, population[i]); // 최소한 이 값은 되어야 함
            hi += population[i];              // 최악은 전부 한 선거구
        }
         
        while (lo <= hi) {
            int mid = (lo + hi) / 2;  // mid = 선거구 최대 허용 인구
             
            if (canDivide(mFrom, mTo, mid, K)) hi = mid - 1;
            else lo = mid + 1;
        }
         
        return lo;
    }
}