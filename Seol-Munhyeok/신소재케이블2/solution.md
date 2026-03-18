# 사고 과정
* 트리 구조를 가진다는 것에 주목
* 장비 번호가 최대 1,000,000,000라는 점에서 긴장했으나 실제로 노드를 추가하는 `connect()` 연산이 10,000 이하이므로 노드 개수는 최대 10,000개라는 점을 확인
    *  장비 번호와 문제 풀 때 사용할 노드 인덱스의 매핑을 저장할  HashMap 필요
* 임의의 두 장비의 전송 경로에 있는 장비의 수는 100 이하라는 조건을 해석해보면 rooted tree를 만들면 트리의 높이가 최대 100이라는 조건으로 해석할 수 있음
   * dfs, bfs를 돌려도 충분하겠다고 생각
---
* 지금 문제에서는 따로 루트를 언급하지는 않았는데 문제를 쉽게 풀려면 루트가 있는 rooted tree를 만드는 것이 좋겠다고 생각
* 두 노드간의 거리를 구하는 `measure()`는 알고리즘 스터디 team1에서 풀었던 [트리와 쿼리 2](https://www.acmicpc.net/problem/13511) 문제에서 아이디어를 얻었음
  * 루트에서부터 노드까지의 거리를 dist 배열에 저장하고, 거리를 구할 두 노드(a, b)의 LCA를 찾으면 a, b 노드 간 거리는 `dist[a] + dist[b] - 2 * dist[LCA]` 이다.
  * 트리의 높이가 최대 100이므로 어려운 방법없이 단순한 방법으로 LCA를 구해도 충분하다.
---
* `test()`에서 고민을 많이 했는데 특정 노드를 포함하는 최장 경로를 어떻게 찾을 지를 고민하였음
* 최단 경로와 달리 그리디한 방법으로는 반례가 존재함을 확인
* 트리의 지름과도 별 상관이 없다고 판단 (특정 노드가 지름 상에 존재하지 않을 수도 있으므로)

* 그래서 이거는 검색을 통해 풀이법을 얻었는데 **특정 노드에서 인접한 모든 방향으로 DFS를 해서 각 방향으로 갈 수 있는 최장 거리를 구하고 가장 긴 거리 + 두 번째로 긴 거리**가 최장 경로임을 알게 되었음

# 자료구조
```java
static final int MAX_NODE = 10000;
int[] parent = new int[MAX_NODE + 5];  	     // 부모 인덱스
int[] depth = new int[MAX_NODE + 5];   		 // 루트로부터 깊이
int[] edgeToParent = new int[MAX_NODE + 5];  // 부모와의 간선 가중치
int[] distRoot = new int[MAX_NODE + 5]; 	 // 루트부터 v까지의 누적 가중치

HashMap<Integer, Integer> idToIdx;  // 장비 번호 -> 내부 노드 인덱스
List<int[]>[] adj;  // {to, weight}
int nodeCount;
```

* `edgeToParent`는 사실 필요없음
* 부모 관계 파악을 위한 `parent`, LCA 찾기 위한 `depth`, 두 노드 사이 거리를 구하기 위한 `distRoot` 정의
* DFS를 사용하기 위해 인접리스트에도 트리를 저장
* `nodeCount`는 `idToIdx` map에 저장할 내부 노드 인덱스 관리용

# `init()`
* 처음에 주어지는 `mDevice`를 루트로 하도록 초기화
* 노드 인덱스는 0-based로 사용

# `connect()`
* `mNewDevice`의 부모를 `mOldDevice`로 하고, 가중치를 `mLatency`로 하도록 간선 추가
* 위에서 정의한 자료구조에도 알맞게 데이터 추가

# `measure()`
* a, b 노드의 LCA를 구하고 `distRoot[a] + distRoot[b] - 2 * distRoot[LCA]`로 두 노드 간 거리를 구함
* 트리의 깊이가 최대 100이므로 LCA 구하는 방법은 O(N)으로 충분함

# `test()`
* 특정 노드에서 인접한 모든 방향으로 DFS 수행
* 각 방향별로 갈 수 있는 최대 거리를 구해서 첫 번째로 큰 값(`firstMax`)과 두 번째로 큰 값(`secondMax`)을 갱신
* 특정 노드에서 인접한 방향이 1개일 수도 있으니 초기 `firstMax`와 `secondMax`는 0으로 초기화
* 답은 `firstMax + secondMax`
