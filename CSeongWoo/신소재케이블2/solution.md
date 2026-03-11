# 신소재케이블2 풀이과정

## 최초 접근 및 설계
- **사고 과정**: 문제의 설명이나 그림 등을 통해 트리를 활용한 문제임을 확인해서 막연히 MST겠거니 생각함. 그러나, 문제의 요구사항에서는 모든 길이 유일함을 명시해서 MST가 아니라 다른 알고리즘(DFS)을 활용하는 문제라고 파악함.
- **자료구조 설계(트리)**: `ArrayList`를 활용해 트리를 구현해야겠다고 생각함.
- **자료구조 최적화(해쉬)**: 문제에서 주어지는 장비 번호의 범위는 `[1 <= mDeviceNum <= 1,000,000,000]`으로, 따로 인덱스를 만들어서 Node에 추가할 생각이었으나, 이러면 해당 Node에 접근하기 위해서 `O(N)`의 탐색시간이 소요됨. 이에 `HashMap<장비번호, 인덱스>()`로 해슁하여 탐색시간을 `O(1)`로 줄임. 물론, 문제의 제약사항을 따져보면 필수는 아님.
- **주요 알고리즘(DFS)**: 문제에서는 **최대 깊이**, 혹은 시작 노드에서 목표 노드까지의 거리 구하기이므로 이에 대한 알고리즘으로는 `DFS`가 맞겠다고 생각함.

## 함수별 동작 원리와 시간 복잡도

### int putNode(int mDevice) 
- `nodeList[nodeCount]`에 새로운 `arrayList` 할당
- `<mDevice, nodeCount>`를 각각 키, 밸류 값으로 할당
- `nodeCount++` 해주면서 return.
- **시간복잡도**: `O(1)`

### void dfs(int from, int endIdx, int len) 
- **목적**: endIdx를 탐색할 때 까지 노드를 탐색
- `flag`: 목표 `endIdx`를 찾은 경우 `true`로 활성화.
- `flag`가 `false`라면: `nodeList[from]`에 연결된 각 노드들을 탐색. 
  - 이때, 방문 예정 노드가 **목표 노드**인 경우 `return`
  - 방문 예정 노드가 **방문하지 않은 노드**인 경우에만 `dfs`
- **시간복잡도**: `O(N)` - 모든 노드를 탐색하는 경우

### void findMaxLen(int from, int len)
- **목적**: 노드의 끝까지 탐색해 가장 거리가 긴 노드 찾기.
- 해당 노드와 연결된 **방문하지 않은** 모든 노드 탐색
- **시간복잡도**: `O(N)`

### void init(int mDevice)
- `nodeCount`를 0으로 초기화하여 매번 장비를 초기화. 기존의 장비는 **덮어씌워지거나, 연결이 끊기게 되므로** 노드 리스트를 초기화하지 않고 `nodeCount`만 초기화
- `HashMap`은 이전 장비번호와 해쉬값이 남아있으므로 초기화 시켜줄 필요가 있음. 
- `putNode(mDevice)`를 통해 해당 장비를 해쉬 추가 + 연결 진행`O(1)`
- **시간복잡도**: `O(1)`

### void connect(int mOldDevice, int mNewDevice, int mLatency)
- 각 노드 값을 해쉬화한 `Idx`로 치환
- 각 노드를 양방향 연결

### int measure(int mDevice1, int mDevice2)
- `DFS`로 `mDevice1`에서 `mDevice2`까지의 길이 탐색하기.
- `queryCount`: 매 dfs마다 visited를 초기화하는 번거로움을 피하기 위해 `timeStamp` 도입. 방문한 노드의 visited값이 `queryCount`와 **같지 않다면** **이번 dfs에서는 방문하지 않았다**는 의미
- `flag`를 false로 처리 -> dfs의 불필요한 탐색 줄이려는 최적화 용도
- dfs 시작
- **시간복잡도**: `O(N)` - dfs 함수의 시간복잡도 따라감

### int test(int mDevice)
- `DFS`로 모든 노드를 탐색하여 최대 길이 구하기.
- `queryCount`: `int measure(int mDevice1, int mDevice2)`에서의 `queryCount`와 같은 용도.
- 탐색 과정
  - 1. 연결된 간선이 한개인 경우
  - 해당 간선에 대해서만 dfs 진행.
  - 2. 연결된 간선이 두개 이상인 경우
  - 모든 연결된 간선을 dfs 진행.
  - 각 간선의 최대 길이를 구하고 `maxLengthList`에 저장
  - `maxLengthList`를 **내림차순**으로 정렬
  - 가장 큰 두 길이를 return
- **시간복잡도**: 모든 노드 **무조건 탐색** O(N) + 정렬 시간(최악의 경우O(N $\log$ N))

