package 국가행정;

import java.util.TreeSet;

class UserSolution_Fenwick {
	
	final int INF = 2_000_000_000;
    final int MAX_N = 10000;
    int N;  // 도시 개수
    int bridgeN; // 다리 개수 (= N - 1)
    int[] population = new int[MAX_N + 1]; // 1-based로 저장
    int[] initDist = new int[MAX_N + 1];   // initDist[i] : i와 i + 1 도시 이동의 초기 시간 저장
    int[] bridgeCnt = new int[MAX_N + 1];  // bridgeCnt[i] : i와 i + 1 도시 사이 다리 개수 저장
    TreeSet<Node> ts = new TreeSet<>();
    Fenwick fw;
    
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
    
    static class Fenwick {
    	int n;
    	int[] tree;
    	
    	Fenwick(int n) {
    		this.n = n;
    		tree = new int[n + 1];  // 1-index
    	}
    	
    	void add(int idx, int val) {
    		while (idx <= n) {
    			tree[idx] += val;
    			idx += idx & -idx;
    		}
    	}
    	
    	int sum(int idx) {
    		int res = 0;
    		while (idx > 0) {
    			res += tree[idx];
    			idx -= idx & -idx;
    		}
    		return res;
    	}
    	
    	int rangeSum(int l, int r) {
    		return sum(r) - sum(l - 1);
    	}
    }
    
	void init(int N, int mPopulation[]) {
		ts.clear();
        
        this.N = N;
        bridgeN = N - 1;
         
        for (int i = 1; i <= N; i++) {
            population[i] = mPopulation[i - 1];  // 주의 : population은 1-based
        }
        
        for (int i = 1; i <= bridgeN; i++) {
        	initDist[i] = population[i] + population[i + 1];
        	bridgeCnt[i] = 1;
        }
        
        fw = new Fenwick(bridgeN);
        
        for (int i = 1; i <= bridgeN; i++) {
        	fw.add(i, initDist[i]);
        	ts.add(new Node(initDist[i], i));
        }
        
		return;
	}

	int expand(int M) {
		int changedDist = 0;
		for (int i = 0; i < M; i++) {
			Node maxNode = ts.pollFirst();
			bridgeCnt[maxNode.idx]++;
			
			changedDist = initDist[maxNode.idx] / bridgeCnt[maxNode.idx];
			int diff = changedDist - maxNode.max;
			
			ts.add(new Node(changedDist, maxNode.idx));
			fw.add(maxNode.idx, diff);
		}
		return changedDist;
	}
	
	// 0-index로 들어옴
	int calculate(int mFrom, int mTo) {
		// 대소관계 보장
        if (mFrom > mTo) {
            int t = mFrom;
            mFrom = mTo;
            mTo = t;
        }
		return fw.rangeSum(mFrom + 1, mTo);
	}
	
	// 0-index로 들어옴
	private boolean canDivide(int mFrom, int mTo, int limit, int K) {
		int sum = 0;
		int cnt = 1;
		
		for (int i = mFrom + 1; i <= mTo + 1; i++) {
			if (sum + population[i] <= limit) sum += population[i];
			else {
				cnt++;
				sum = population[i];
			}
		}
		
		return cnt <= K;
	}
	
	// 0-index로 들어옴
	int divide(int mFrom, int mTo, int K) {
		int lo = 0;
		int hi = 0;
		
		// 1-index로 보정
		for (int i = mFrom + 1; i <= mTo + 1; i++) {
			lo = Math.max(lo, population[i]);
			hi += population[i];
		}
		
		while (lo <= hi) {
			int mid = (lo + hi) / 2;
			if (canDivide(mFrom, mTo, mid, K)) hi = mid - 1;
			else lo = mid + 1;
		}
		return lo;
	}
}