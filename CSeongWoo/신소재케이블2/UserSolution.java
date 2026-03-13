import java.util.*;

/**
 * 그냥 트리 구현하기. 이를 DFS로 노드 탐색 구현하기. 노드는 최대 1만1개가 있음. 
 * DFS는 어차피 노드가 총 1만개, 그리고 이 노드간의 길이 자체는 100을 넘지 못함.
 * 노드 클래스 구현
 */
class UserSolution
{
	class Node {
		int idx, weight;
		Node(int idx, int weight) {
			this.idx = idx;
			this.weight = weight;
		}
	}
	
	static List<Node>[] nodeList = new ArrayList[10001];
	HashMap<Integer, Integer> numToIdx = new HashMap<>(); // 키값 = 장비번호, 밸류값 = 인덱스
	static int nodeCount; // 노드 개수 - 인덱스는 0부터 시작하므로, 노드 개수 = 다음 노드가 들어갈 인덱스
	// DFS 방문 체크 배열 - 매 dfs마다 초기화해야하므로, boolean이 아닌 int로 구현. queryCount로 방문 여부 체크.
	static int visited[] = new int[10001]; 
	static int queryCount;
	
	static int dfsAns; // measure의 답이 될 dfs
	static boolean flag; // dfs를 멈출 플래그 - 최적화용
	
	static int maxAns; // 한 노드에서 시작된 최대길이 찾기 dfs 값- test
	
	
	int putNode(int mDevice) {
		nodeList[nodeCount] = new ArrayList<>();
		numToIdx.put(mDevice, nodeCount);
		return nodeCount++;
	}
	
	void dfs(int from, int endIdx, int len) {
		if (flag) return; // 탐색 완료 -> 모든 dfs 종료
		for(Node to : nodeList[from]) {
			int nextLen = len + to.weight;
			if (to.idx == endIdx) {
				dfsAns = nextLen;
				flag = true;
				return;
			}
			if (visited[to.idx] != queryCount) {
				visited[to.idx] = queryCount;
				dfs(to.idx, endIdx, nextLen);
			}
		}
		
		return;
	}
	// 불린을 쓰는 경우
//    boolean dfs(int from, int endIdx, int len) {
//        for(Node to : nodeList[from]) {
//            int nextLen = len + to.weight;
//            // 목표 도달 -> return true;
//            if (to.idx == endIdx) { 
//                dfsAns = nextLen;
//                return true;
//            }
//             
//            if (!visited[to.idx]) {
//                visited[to.idx] = true;
//                if (dfs(to.idx, endIdx, nextLen)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
	
	// 한 노드에서 시작된 최대 길이를 찾는 dfs
	void findMaxLen(int from, int len) {
		maxAns = Math.max(maxAns, len);
		for(Node to : nodeList[from]) {
			int nextLen = len + to.weight;
			if (visited[to.idx] != queryCount) {
				visited[to.idx] = queryCount;
				findMaxLen(to.idx, nextLen);
			}
		}
	}
	
	public void init(int mDevice)
	{
		// nodeCount는 0으로 초기화.
		// 어차피 남아있는 노드는 덮어씌워지거나, 연결이 끊기므로 노드 리스트는 초기화할 필요 없음.
		nodeCount = 0;
		// numToIdx는 초기화.
		// 왜냐하면 numToIdx
		numToIdx.clear();
		putNode(mDevice);
		
		return;
	}
	
	
	public void connect(int mOldDevice, int mNewDevice, int mLatency)
	{
		// 각 노드 값을 해쉬화한 nodeCount로 치환.
		int oldIdx = numToIdx.get(mOldDevice);
		int newIdx = putNode(mNewDevice);
		// 노드에는 양방향 연결, 식별은 치환된 인덱스로
		nodeList[oldIdx].add(new Node(newIdx, mLatency));
		nodeList[newIdx].add(new Node(oldIdx, mLatency));
		return;
	}
	
	// DFS로 mDevice1에서 mDevice2까지의 길이 탐색하기.
	public int measure(int mDevice1, int mDevice2)
	{
		// 매 dfs마다 visited 초기화
		queryCount++;
		// 전역변수 초기화.
		dfsAns = 0;
		int start = numToIdx.get(mDevice1);
		int end = numToIdx.get(mDevice2);
		flag = false;
		// 시작 노드 방문 처리
		visited[start] = queryCount;
		dfs(start, end, 0);
//		System.out.println("measure: " + dfsAns);
		return dfsAns;
	}
	
	public int test(int mDevice)
	{
		// 매 dfs마다 visited 초기화
		queryCount++;
		int idx = numToIdx.get(mDevice);
		visited[idx] = queryCount;
		if (nodeList[idx].size() <= 1) { // 연결된 노드가 한개 이하인 경우
			maxAns = 0;
			findMaxLen(idx, 0);
//			System.out.println("test1: device " + mDevice + " ans: " + maxAns);
			return maxAns;
		}
		else {
			ArrayList<Integer> maxLengthList = new ArrayList<>();
			// 각 연결된 노드에서 시작된 최대 길이를 구해서, 가장 긴 두 길이를 더하기.
			for(Node to : nodeList[idx]) {
				int len = to.weight;
				maxAns = 0;
				visited[to.idx] = queryCount;
				findMaxLen(to.idx, len);
				int ans = maxAns;
				maxLengthList.add(ans);
			}
			// 내림차순 정렬
			Collections.sort(maxLengthList, (o1, o2) -> Integer.compare(o2, o1));
			int ans = maxLengthList.get(0) + maxLengthList.get(1);
//			System.out.println("test2: device " + mDevice + " ans: " + ans);
			return ans;
		}
	}
}