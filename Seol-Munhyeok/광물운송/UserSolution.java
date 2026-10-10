package 광물운송;

import java.util.ArrayList;

class UserSolution {
	
	static final int MAX = 20005;
	
	int L, N, nodeCnt, bucketCnt;
	
	int[] row = new int[MAX];
	int[] col = new int[MAX];
	int[] qty = new int[MAX];
	int[] id = new int[MAX];
	
	// Union-Find
	int[] parent = new int[MAX];
	int[] size = new int[MAX];
	
	// 연결요소 정보
	int[] sum = new int[MAX];  // 연결요소의 총 광물량
	int[] best = new int[MAX]; // 연결요소 안에서 "우선순위 최고 캠프" 
	
	ArrayList<Integer>[][] bucket;
	
	void init(int L, int N) {
		this.L = L;
		this.N = N;
		this.nodeCnt = 0;
		
		// 버킷 한 칸의 한 변 길이를 L로 잡음
        // 전체 버킷 개수 = ceil(N / L)
		this.bucketCnt = (N + L - 1) / L;
		bucket = new ArrayList[bucketCnt][bucketCnt];
		
		for (int i = 0; i < bucketCnt; i++) {
		    for (int j = 0; j < bucketCnt; j++) {
		        bucket[i][j] = new ArrayList<>();
		    }
		}
	}
	
	private int better(int a, int b) {
		if (a == 0) return b;
	    if (b == 0) return a;
	    
		if (qty[a] != qty[b]) return qty[a] < qty[b] ? a : b;
		if (row[a] != row[b]) return row[a] < row[b] ? a : b;
		if (col[a] != col[b]) return col[a] < col[b] ? a : b;
		return id[a] < id[b] ? a : b;
	}
	
	private int find(int x) {
		if (parent[x] == x) return x;
		return parent[x] = find(parent[x]);
	}
	
	// union 후 최종 root를 반환
	private int union(int a, int b) {
		int ra = find(a);
		int rb = find(b);
		if (ra == rb) return ra;
		
		// size 기준 union (ra가 더 크게)
		if (size[ra] < size[rb]) {
			int tmp = ra;
			ra = rb;
			rb = tmp;
		}
		
		parent[rb] = ra;
		
		size[ra] += size[rb];
		
		// 광물 합 합치기
		sum[ra] += sum[rb];
		
		// best 갱신
		best[ra] = better(best[ra], best[rb]);
		
		return ra;
	}
	
	int addBaseCamp(int mID, int mRow, int mCol, int mQuantity) {
		int idx = ++nodeCnt;
		
		// 새 노드 정보 저장
		id[idx] = mID;
		row[idx] = mRow;
		col[idx] = mCol;
		qty[idx] = mQuantity;
		
		// DSU 초기화
		parent[idx] = idx;
		size[idx] = 1;
		sum[idx] = mQuantity;
		best[idx] = idx;
		
		// 현재 노드가 들어갈 버킷 좌표
		int br = mRow / L;
		int bc = mCol / L;
		
		// 주변 3*3 버킷만 검사
		int rFrom = Math.max(0, br - 1);
		int rTo = Math.min(bucketCnt - 1, br + 1);
		int cFrom = Math.max(0, bc - 1);
		int cTo = Math.min(bucketCnt - 1, bc + 1);
		
		for (int r = rFrom; r <= rTo; r++) {
			for (int c = cFrom; c <= cTo; c++) {
				for (int other : bucket[r][c]) {
					int dist = Math.abs(row[other] - mRow) + Math.abs(col[other] - mCol);
					if (dist <= L) {
						union(idx, other);
					}
				}
			}
		}
		
		// 모든 검사 후 자기 버킷에 삽입
		bucket[br][bc].add(idx);
		
		// 새 노드가 속한 연결요소 총합 반환
		return sum[find(idx)];
	}
	
	int findBaseCampForDropping(int K) {
		int ans = 0;
		
		for (int i = 1; i <= nodeCnt; i++) {
			// 루트만 확인
			if (parent[i] != i) continue;
			
			if (sum[i] >= K) {
				ans = better(ans, best[i]);
			}
		}
		
		return ans == 0 ? -1 : id[ans];
	}

}
