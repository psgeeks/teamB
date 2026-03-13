package 신소재케이블2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class UserSolution {
	
	// tree의 from정점과 to정점을 연결하는 간성과 가중치
	// tree[from] -> edge(to, weight)
    static class Edge {
        int to, weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
    
    // HashMap의 탐색 속도는 O(1)이라고 합니다. ㄴㅇㄱ
    Map<Integer, Integer> deviceMap;
    
    // 정점과 연결된 간선을 저장할 자료구조
    // tree[정점][간선]
    List<List<Edge>> tree;
    
    // 트리에 저장된 정점의 개수
    int treeSize;

    public void init(int mDevice) {
        deviceMap = new HashMap<>();
        tree = new ArrayList<>();
        treeSize = 0;
        
        // 시작 정점 하나 트리에 삽입
        deviceMap.put(mDevice, treeSize++);
        
        // 시작 정점에 해당하는 간선 리스트 생성
        tree.add(new ArrayList<>());
    }

    public void connect(int mOldDevice, int mNewDevice, int mLatency) {
        // oldDevice는 이미 저장되어 있으므로 트리 인덱스만 가져옴
    	int oldIdx = deviceMap.get(mOldDevice);
        
    	// newDevice는 트리에 없으므로 생성해야함
        int newIdx = treeSize++;
        deviceMap.put(mNewDevice, newIdx);
        tree.add(new ArrayList<>());
        
        // 무방향 트리 간선 연결
        // oldDevice와 newDevice 모두에 간선 정보 저장
        tree.get(oldIdx).add(new Edge(newIdx, mLatency));
        tree.get(newIdx).add(new Edge(oldIdx, mLatency));
    }

    public int measure(int mDevice1, int mDevice2) {
    	// 탐색할 두 정점의 트리 인덱스 불러오기
        int start = deviceMap.get(mDevice1);
        int target = deviceMap.get(mDevice2);
        
        // 두 정점 사이 거리 계산 함수 호출
        return getDist(start, target);
    }

    public int test(int mDevice) {
        // startNode를 지나는 최장 경로 구하기
    	// 그렇다면 startNode를 시작 정점으로 했을 때
    	// 가장 먼 정점까지의 거리 2개의 합을 구하면 됨
    	// 단, 중복 정점을 지나면 안 됨
    	int startNode = deviceMap.get(mDevice);
        
        // 가장 먼 정점까지의 길이 (max1 >= max2)
        int max1 = 0;
        int max2 = 0; 
        
        // startNode에서 가장 먼 정점 구하기
        for (Edge edge : tree.get(startNode)) {
            int maxDist = getMaxDist(edge.to, startNode) + edge.weight;
            
            // max1보다 크거나 같은 경우
            // max1 -> max2
            // newMax -> max1
            if (maxDist >= max1) {
                max2 = max1;
                max1 = maxDist;
            } 
            // max1보다 작고 max2보다 큰 경우
            // newMax -> max2
            else if (maxDist > max2) {
                max2 = maxDist;
            }
        }
        
        // 가장 긴 두 정점까지의 거리 합 반환
        return max1 + max2;
    }

    // 두 정점 사이의 거리를 계산하기 위한 함수 (BFS)
    private int getDist(int start, int target) {
        Queue<int[]> q = new LinkedList<>();
        boolean[] visited = new boolean[treeSize];
        
        // 시작 점을 q에 삽입
        q.add(new int[]{start, 0});
        visited[start] = true;
        
        while (!q.isEmpty()) {
            int[] curr = q.poll();
            int node = curr[0];
            int dist = curr[1];
            
            // 현재 노드가 target 노드면 거리 반환
            if (node == target) return dist;
            
            for (Edge edge : tree.get(node)) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    q.add(new int[]{edge.to, dist + edge.weight});
                }
            }
        }
        return -1;
    }

    // startNode와 parentNode를 지나는 최대 거리
    // 탐색은 parentNode -> startNode -> ... -> endNode 순으로 진행
    private int getMaxDist(int startNode, int parentNode) {
        Queue<int[]> q = new LinkedList<>();
        boolean[] visited = new boolean[treeSize];
        
        q.add(new int[]{startNode, 0});
        visited[startNode] = true;
        
        // parentNode를 방문으로 표시하는 이유
        // 현재 startNode가 parentNode의 다른 startNode와 같은 간선을 지나지 않게 하기 위함
        visited[parentNode] = true;
        
        int maxDist = 0;
        
        // 아래는 보통 BFS와 동일
        while (!q.isEmpty()) {
            int[] curr = q.poll();
            int node = curr[0];
            int dist = curr[1];
            
            maxDist = Math.max(maxDist, dist);
            
            for (Edge edge : tree.get(node)) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    q.add(new int[]{edge.to, dist + edge.weight});
                }
            }
        }
        return maxDist;
    }
}