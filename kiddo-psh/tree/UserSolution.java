
import java.util.*;

class UserSolution
{
	class Edge {
		int to, w;
		Edge(int to, int w) {this.to=to; this.w=w;}
	}
	
	final int ROOT = 1;
	final int MAX_N = 10_000;
	
	@SuppressWarnings("unchecked")
	List<Edge>adj[] = new ArrayList[MAX_N+1];
	Map<Integer, Integer> idToIndex = new HashMap<>();
	
	int deviceIdx = 0;
	int[] parent = new int[MAX_N+1];
	int[] depth = new int[MAX_N+1];
	int[] dist = new int[MAX_N+1];
	
	int farthestNode;
	int maxDist;
	
	public void init(int mDevice)
	{
		for (int i=0; i<=MAX_N; i++) {
			Arrays.fill(parent, 0);
			depth[i] = 0;
			dist[i] = 0;
			if (adj[i] == null) adj[i] = new ArrayList<>();
			else adj[i].clear();
		}
		deviceIdx = 1;
		idToIndex.put(mDevice, deviceIdx);
		parent[deviceIdx] = 0;
		depth[deviceIdx] = 0;
		dist[deviceIdx] = 0;
		
		return;
	}
	
	
	public void connect(int mOldDevice, int mNewDevice, int mLatency)
	{
		deviceIdx++; // new가 쓸 인덱스
		idToIndex.put(mNewDevice, deviceIdx);
		
		int newIdx = deviceIdx;
		int oldIdx = idToIndex.get(mOldDevice);
		
		adj[oldIdx].add(new Edge(newIdx, mLatency));
		adj[newIdx].add(new Edge(oldIdx, mLatency));
		
		dist[newIdx] = dist[oldIdx] + mLatency;
		depth[newIdx] = depth[oldIdx]+1;
		parent[newIdx] = oldIdx;

		return;
	}
	
	public int measure(int mDevice1, int mDevice2) // 두 정점 사이 거리 
	{
		//@a->b 경로 : a -> LCA(a,b) -> b
		//@a->b 거리 : dist[a] + dist[b] - 2*dist[LCA(a,b)]
		int a = idToIndex.get(mDevice1);
		int b = idToIndex.get(mDevice2);
		
		if (depth[a] < depth[b]) {int t=a; a=b; b=t;}
		
		int distA = dist[a];
		int distB = dist[b];
		
		int diff = depth[a] - depth[b];
		for(int i=0; i<diff; i++) {
			a = parent[a];
		}
		
		if (a==b) return distA - distB;
		
		while (a!=b) {
			a = parent[a];
			b = parent[b];
		}
		
		int distLCA = dist[a];
		
		return distA + distB - 2*distLCA;
	}
	
	public int test(int mDevice)
	{
		//@깊이 순 2개 찾기
		int root = idToIndex.get(mDevice);

		int first = 0;
		int second = 0;
		for (Edge e : adj[root]) {
			farthestNode = 0;
			maxDist = 0;

			int child = e.to;
			dfs(child, root, e.w);

			if (first < maxDist) {
				second = first;
				first = maxDist;
			} else if (second < maxDist) {
				second = maxDist;
			}
		}
		
		return first+second;
	}

	void dfs(int u, int p, int dist) {
		if (dist > maxDist) {
			maxDist = dist;
			farthestNode = u;
		}

		for (Edge e : adj[u]) {
			int v = e.to;
			if (v == p) continue;

			dfs(v, u, dist+e.w);
		}
	}
}