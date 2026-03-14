package 신소재케이블2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 메모리: 102,288kb 실행시간:433ms
 */
class UserSolution
{
	static final int MAX = 10005; //최대 장비 수
	static final int LOG = 14; //2^14 > 16384
	
	static class Edge{
		int to, w;
		Edge(int to,int w){
			this.to = to;
			this.w = w;
		}
	}
	
	HashMap<Integer,Integer> idMap;
	int nodeCnt;
	
	int[][] parent; //parent[k][v] v의 2^k번째 조상
	int[] depth; //루트로부터 깊이
	long[] distRoot; //루트로부터 거리합
	ArrayList<Edge> graph[];
	
	//DFS용 스택 배열
	int[] stackNode;
	int[] stackParent;
	long[] stackDist;
	
	//장비 번호 -> 내부 인덱스
	int addDevice(int deviceId) {
		
		idMap.put(deviceId, ++nodeCnt);
		graph[nodeCnt] = new ArrayList<>();
		return nodeCnt;
	}
	
	public void init(int mDevice)
	{
		idMap = new HashMap<>(MAX);
		nodeCnt = 0;
		
		parent = new int[LOG][MAX];
		depth = new int[MAX];
		distRoot = new long[MAX];
		graph = new ArrayList[MAX];
		
		stackNode = new int[MAX];
		stackParent = new int[MAX];
		stackDist = new long[MAX];
		
		int root = addDevice(mDevice);
		
		depth[root] = 0;
		distRoot[root] = 0;
		
		return;
	}
	
	// 기존 장비 mOldDevice에 새 장비 mNewDevice를 latency로 연결
	public void connect(int mOldDevice, int mNewDevice, int mLatency)
	{
		int u = idMap.get(mOldDevice); //기존 장비 
		int v = addDevice(mNewDevice); //새 장비 추가
		
		//트리에 추가
		graph[u].add(new Edge(v,mLatency)); 
		graph[v].add(new Edge(u,mLatency));
		
		//LCA용 정보 갱신
		parent[0][v] = u;
		depth[v] = depth[u] + 1;
		distRoot[v] = distRoot[u] + mLatency;
		
		for(int k=1;k<LOG;k++) {
			int pre = parent[k-1][v];
			parent[k][v] = parent[k-1][pre];
		}
		return;
	}
	
	int LCA(int a,int b) {
		if(depth[a]<depth[b]) {
			int temp = a;
			a = b;
			b = temp;
		}
		// a를 b와 같은 깊이로
		for(int k=LOG-1;k>=0;k--) {
			if(depth[a]-(1<<k)>=depth[b]) {
				a = parent[k][a];
			}
		}
		if(a==b) return a;
		
		//두 노드를 같이 올리면서 LCA 바로 아래까지 이동
		for(int k=LOG-1;k>=0;k--) {
			if(parent[k][a] != parent[k][b]) {
				a = parent[k][a];
				b = parent[k][b];
			}
		}
		return parent[0][a];
	}
	
	// 두 장비 사이 전송 시간 반환
	public int measure(int mDevice1, int mDevice2)
	{
		int a = idMap.get(mDevice1);
		int b = idMap.get(mDevice2);
		
		int c = LCA(a,b);
		long dist = distRoot[a] + distRoot[b] - 2L * distRoot[c];
		return (int) dist;
	}
	
	//s 방향(부모 ban)으로 갔을 때 x로부터의 최대 거리 
	//start: x의 어떤 이웃 방향으로 한 칸 들어간 위치
	//ban: x < 다시 돌아가는 것 금지
	//initialWeight: e.w x에서 그 이웃까지 간 비용부터 시작
	long MaxDist(int start,int ban,int initialWeight) {
		int top = 0;
		stackNode[top] = start;
		stackParent[top] = ban;
		stackDist[top] = initialWeight;
		top ++;
		
		long maxDist = 0;
		
		while(top>0) {
			top --;
			int cur = stackNode[top];
			int par = stackParent[top];
			long dist = stackDist[top];
			
			if(dist>maxDist) maxDist = dist;
			
			for(Edge e : graph[cur]) {
				if(e.to == par) continue; //부모방향으로는 다시 가지 않음
				
				stackNode[top] = e.to;
				stackParent[top] = cur;
				stackDist[top] = dist+e.w;
				top ++;
			}
		}
		
		return maxDist;
	}
	
	// mDevice를 지나는 최대 전송 시간
	// 가장 긴 방향 + 두번째로 긴 방향
	public int test(int mDevice)
	{
		int x = idMap.get(mDevice);
		
		long best1 = 0; //가장 긴 방향 거리
		long best2 = 0; //두 번째로 긴 방향 거리
		
		// x의 각 이웃 방향별 최대거리 계산
		for (Edge e: graph[x]) {

			long dist = MaxDist(e.to, x, e.w);
			
			if (dist>best1) {
				best2 = best1;
				best1 = dist;
			}else if (dist>best2) {
				best2 = dist;
			}
		}
		
		return (int)(best1 + best2);
	}
}