
import java.util.*;

class UserSolution
{
	static final int MAX_DEVICE_CNT = 10005;
	static final int MAX_LOG = 15;
	
	int mCnt = 0; // 현재 존재하는 정점의 개수 
	List<Device>[] edges; // edges[i] : i번 정점에 연결된 정점정보
	Map<Integer, Integer> mDeviceToIdx = new HashMap<>(); // key:mDevice, value:mIdx(매핑된 idx num)
	int[][] parent;
	int[] depth;
	int[] distFromRoot;
	
	static class Device{
		int mIdx; // 연결된 정점
		int d; // 간선 거리
		Device(int mIdx, int d){
			this.mIdx = mIdx;
			this.d = d;
		}
	}
	
	public void init(int mDevice)
	{
		mCnt = 0;
		mDeviceToIdx.clear();
		
		// 배열 초기화 
		depth = new int[MAX_DEVICE_CNT];
		parent = new int[MAX_DEVICE_CNT][MAX_LOG];
		distFromRoot = new int[MAX_DEVICE_CNT];
		Arrays.fill(distFromRoot, Integer.MAX_VALUE);
		edges = new List[MAX_DEVICE_CNT];
		for(int i = 0; i < MAX_DEVICE_CNT; i++) {
			edges[i] = new ArrayList<>();
		}
		
		// 첫번째 정점은 mCnt(0) 으로 매핑 정보 등록 
		mDeviceToIdx.put(mDevice, mCnt++);
		depth[0] = 0;
		distFromRoot[0] = 0;
		return;
	}
	
	
	public void connect(int mOldDevice, int mNewDevice, int mLatency)
	{
		// 새로 추가하려는 디바이스 idx 매핑 정보 등록
		mDeviceToIdx.put(mNewDevice, mCnt++);

		int mOldIdx = mDeviceToIdx.get(mOldDevice);
		int mNewIdx = mDeviceToIdx.get(mNewDevice);
		
		parent[mNewIdx][0] = mOldIdx;
		depth[mNewIdx] = depth[mOldIdx]+1;
		distFromRoot[mNewIdx] = distFromRoot[mOldIdx] + mLatency;
		for(int log = 1; log < MAX_LOG; log++) {
			parent[mNewIdx][log] = parent[parent[mNewIdx][log-1]][log-1];
		}
		
		// mOldDevice <-> mNewDevice 경로 추가 
		edges[mOldIdx].add(new Device(mNewIdx, mLatency));
		edges[mNewIdx].add(new Device(mOldIdx, mLatency));
		
		return;
	}
	
	private int getLCAIdx(int a, int b) {
		// a의 depth를 더 밑에 하도록 세팅
		if(depth[a] < depth[b]) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		// depth 맞추기 
		for(int log = MAX_LOG-1; log >= 0; log--) {
			if(depth[a] - (1<<log) >= depth[b]) {
				a = parent[a][log];
			}
		}
		
		// depth 맞췃을 때 동일하면 이게 바로 LCA 
		if(a == b ) return a;
		
		for(int log = MAX_LOG-1; log >= 0; log--) {
			if(parent[a][log] != parent[b][log]) {
				a = parent[a][log];
				b = parent[b][log];
			}
		}
		return parent[a][0];
	}
	
	public int measure(int mDevice1, int mDevice2)
	{
		int mIdx1 = mDeviceToIdx.get(mDevice1);
		int mIdx2 =  mDeviceToIdx.get(mDevice2);
		int lca = getLCAIdx(mIdx1, mIdx2);
		return distFromRoot[mIdx1] + distFromRoot[mIdx2] - 2 * distFromRoot[lca];
	}
	
	// start에서 가장 긴 길이를 return
	int getMaxDist(int start, boolean[] visited) {
		Queue<Device> q = new ArrayDeque<>();
		q.add(new Device(start, 0));
		visited[start] = true;
		int res = 0;
		while(!q.isEmpty()) {
			Device cur = q.poll();
			
			res = Math.max(res, cur.d);
			
			for(Device next : edges[cur.mIdx]) {
				if(visited[next.mIdx]) continue;
				visited[next.mIdx] = true;
				q.add(new Device(next.mIdx, next.d+cur.d));
			}
		}
		return res;
	}
	
	public int test(int mDevice)
	{	
		int mIdx = mDeviceToIdx.get(mDevice);
		boolean[] visited = new boolean[mCnt];
		Arrays.fill(visited, false);
		visited[mIdx] = true;
		
		PriorityQueue<Integer> pq = new PriorityQueue<>((a, b)->b-a);
		for(Device adj : edges[mIdx]) {
			int cur = getMaxDist(adj.mIdx, visited) + adj.d;
			pq.add(cur);
		}
		if(pq.size() >= 2) return pq.poll()+pq.poll();
		else return pq.poll();
	}
}