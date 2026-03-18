package 신소재케이블2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class UserSolution {
	
	static final int MAX_NODE = 10000;
	int[] parent = new int[MAX_NODE + 5];  	     // 부모 인덱스
	int[] depth = new int[MAX_NODE + 5];   		 // 루트로부터 깊이
	int[] edgeToParent = new int[MAX_NODE + 5];  // 부모와의 간선 가중치
	int[] distRoot = new int[MAX_NODE + 5]; 	 // 루트부터 v까지의 누적 가중치
	
	HashMap<Integer, Integer> idToIdx;  // 장비 번호 -> 내부 노드 인덱스
	List<int[]>[] adj;  // {to, weight}
	int nodeCount;
	
	public void init(int mDevice) {
		idToIdx = new HashMap<>();
		adj = new List[MAX_NODE + 5];
		for (int i = 0; i < MAX_NODE + 5; i++) adj[i] = new ArrayList<>();
		
		nodeCount = 1;
		idToIdx.put(mDevice, 0);  // 노드 0-based
		
		parent[0] = -1;
		depth[0] = 0;
		edgeToParent[0] = 0;
		distRoot[0] = 0;
		return;
	}
	
	public void connect(int mOldDevice, int mNewDevice, int mLatency) {
		int newIdx = nodeCount;
		int oldIdx = idToIdx.get(mOldDevice);
		idToIdx.put(mNewDevice, nodeCount++);
		
		parent[newIdx] = oldIdx;
		depth[newIdx] = depth[oldIdx] + 1;
		edgeToParent[newIdx] = mLatency;
		distRoot[newIdx] = distRoot[oldIdx] + mLatency;
		
		adj[oldIdx].add(new int[] {newIdx, mLatency});
		adj[newIdx].add(new int[] {oldIdx, mLatency});
		return;
	}
	
	private int getLCA(int a, int b) {
		// 항상 a가 더 위에 있도록
		if (depth[a] > depth[b]) { int t; t = a; a = b; b = t; }
		
		// a와 depth가 같을 때 까지 b가 올라감
		while (depth[b] != depth[a]) {
			b = parent[b];
		}
		
		// 같은 노드를 만날 때까지 한 칸씩 올라감
		while (a != b) {
			a = parent[a];
			b = parent[b];
		}
		
		return a;
	}
	
	public int measure(int mDevice1, int mDevice2) {
		int idx1 = idToIdx.get(mDevice1);
		int idx2 = idToIdx.get(mDevice2);
		int lca = getLCA(idx1, idx2);
		
		return distRoot[idx1] + distRoot[idx2] - 2 * distRoot[lca];
	}
	
	// cur에서 시작해서 갈 수 있는 최대 거리 찾기
	private int dfs(int cur, int par) {
		int max = 0;
		
		for (int[] edge : adj[cur]) {
			int next = edge[0], w = edge[1];
			if (next == par) continue;
			
			max = Math.max(max, dfs(next, cur) + w);
		}
		
		return max;
	}
	
	public int test(int mDevice) {
		int x = idToIdx.get(mDevice);
		int firstMax = 0;
		int secondMax = 0;
		
		for (int[] edge : adj[x]) {
			int next = edge[0], w = edge[1];
			int dist = dfs(next, x) + w;
			
			if (dist > firstMax) {
				secondMax = firstMax;
				firstMax = dist;
			}
			else if (dist < firstMax && dist > secondMax) {
				secondMax = dist;
			}
		}
		
		return firstMax + secondMax;
	}
}