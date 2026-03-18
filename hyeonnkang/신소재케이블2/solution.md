# 접근 과정

## 문제 분석
- `init()`에서 처음 디바이스 1개가 생성된다.
- 이후 `connect()`가 최대 **10,000번 호출**된다.
- 따라서 전체 디바이스 개수의 최대는 10,001개이다.
- 또한 `connect(old, new)` 형태이므로 **항상 - 새로운 노드가 기존 노드 하나에 연결된다.**  
- 즉 그래프는 트리이다.

## 첫 번째 시도

### 노드 번호 매핑
1 ≤ mDevice ≤ 1,000,000,000 이므로 배열 인덱스로 직접 사용하기 어렵다.

따라서 `HashMap<mDevice, mIdx> mDeviceToIdx`을 사용하여 실제 디바이스 번호를 0 ~ N-1 인덱스로 매핑

### 그래프 저장 방식

간선 정보는 **인접 리스트**로 이용

```
edges[mIdx] : mIdx와 연결된 간선(mIdx, latency) 리스트
```

### `measure(a, b)`
두 노드 사이의 최단 거리를 반환해야 한다.

처음에는 플로이드 워셜(시간복잡도: O(N³)), 다익스트라(시간복잡도: O(E log V)) 알고리즘을 고려

하지만 복잡도가 너무 큼.

-> 모든 쌍 최단거리를 미리 계산해 두자.

```
dist[i][j] : i → j 최단 거리
```

`connect()`가 호출될 때마다

```
dist[다른노드][new] = dist[다른노드][old] + dist[old][new]
```

### `test(x)`

트리에서 어떤 정점을 지나는 가장 긴 경로는
```
(한 방향 서브트리 최대 거리) + (다른 방향 서브트리 최대 거리)
```
따라서
1. `x`와 연결된 모든 인접 노드 확인
2. 각 방향으로 BFS 수행
3. 해당 방향에서의 최대 거리 계산
4. 가장 큰 두 값을 더함

구현 방식

```
for (x와 연결된 각 노드) {
    BFS 수행
    해당 방향 최대 거리 계산
}

가장 큰 두 값 선택
```

## 문제점 발생

`dist` 배열의 크기는 10001 × 10001

`int` 기준 메모리는 약 400MB 이상

Java에서는 배열 객체 오버헤드까지 포함되기 때문에 메모리 초과가 발생


## 두 번째 시도 (with AI)

이때까지 트리라는 정보를 전혀 이해하지 못했다.
트리에서 LCA를 이용해 두 정점의 거리를 구할 수 있다는 힌트를 받았다.

### 트리 거리 공식

트리에서 두 노드 `i`, `j` 사이 거리

`distFromRoot[x]` : 루트 → x 거리

`LCA(i, j)`       : i와 j의 최소 공통 조상

```
dist(i, j) = distFromRoot[i] + distFromRoot[j] - 2 × distFromRoot[LCA(i, j)]
```

### 저장해야 하는 정보

```
depth[i]         : 루트 기준 깊이
distFromRoot[i]  : 루트부터 누적 거리
parent[i][k]     : i의 2^k번째 조상
edges[i]         : 인접 리스트
```

따라서 `connect()`, `measure(a, b)` 구현을 아래처럼 수정
### `connect()`

```
parent[new][0] = old
depth[new] = depth[old] + 1
distFromRoot[new] = distFromRoot[old] + latency
```

그리고 LCA를 구할 때 2^k 씩 점프하기 위해

```
parent[new][k] = parent[parent[new][k-1]][k-1]
```

### `measure(a, b)`

1. 두 노드 깊이 맞추기
2. LCA 찾기
3. 거리 공식 적용

```
dist = distFromRoot[a] + distFromRoot[b] - 2 * distFromRoot[lca]
```

시간복잡도는 O(log N)