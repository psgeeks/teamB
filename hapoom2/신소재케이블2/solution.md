# 사용한 자료구조

## 1. 트리 인접 리스트

```
ArrayList<Edge>[]graph
```

각 장비는 트리 형태로 연결됩니다.

```
Edge
to : 연결된 장비
w  : 케이블 전송 시간
```

트리는 양방향 그래프이므로 `u ↔ v`두 방향으로 간선을 저장합니다.

장비 간 연결 관계를 저장하고 `test()`에서 특정 방향으로 탐색하기 위해 사용합니다.

---

## 2. HashMap (장비 번호 압축)

```
HashMap<Integer,Integer>idMap
idMap.put(deviceId, ++nodeCnt);
```

장비 번호 범위가 1 ~ 1,000,000,000 이므로 배열 인덱스로 직접 사용할 수 없습니다.

그래서 HashMap을 사용해 deviceId → 내부 index 형태로 변환합니다.

ex) 1000000000 → 1, 5 → 2

---

## 3. LCA 테이블 (Binary Lifting LCA)

```
parent[k][v]: v의 2^k 번째 조상
parent[0][v] = 바로 부모
parent[1][v] = 2칸 위
parent[2][v] = 4칸 위
```

두 장비 사이의 거리를 빠르게 구하기 위해 Binary Lifting을 이용한 **LCA 알고리즘**을 사용합니다.

---

## 4. 깊이와 루트 거리 공식

```
depth[v]    : 루트에서 v까지 깊이
distRoot[v] : 루트에서 v까지 거리 합
dist(a,b) = distRoot[a] + distRoot[b] - 2 * distRoot[LCA(a,b)]
```

두 노드 사이 거리 계산을 위 공식으로 처리합니다.

---

## 5. DFS 스택 (배열 스택)

```
stackNode[]
stackParent[]
stackDist[]
```

재귀 대신 **배열 스택을 이용한 DFS**를 사용합니다.

- 재귀 호출 제거
- 객체 생성 감소

→ 빠름

---

# 함수별 정리

## 1. init(mDevice)

초기 장비 하나를 생성하는 함수입니다.

- 장비 번호 압축을 위한 `HashMap` 초기화
- LCA 테이블과 트리 배열 초기화
- DFS 스택 배열 생성
- `mDevice`를 루트 장비로 등록

```
introot=addDevice(mDevice);
depth[root]=0;
distRoot[root]=0;
```

---

# 2. addDevice(deviceId)

새 장비를 내부 인덱스로 등록합니다.

```
idMap.put(deviceId,++nodeCnt);
```

새 장비 번호를 내부 인덱스로 변환합니다.

장비 번호 범위가 매우 크기 때문에 **번호 압축**을 수행합니다.

---

# 3. connect(mOldDevice, mNewDevice, mLatency)

새 장비를 기존 장비에 연결하는 함수입니다.

1. 트리 연결

```
graph[u].add(newEdge(v,latency));
graph[v].add(newEdge(u,latency));
```

2. 기본 정보 설정

```
parent[0][v]=u
depth[v]=depth[u]+1
distRoot[v]=distRoot[u]+latency
```

3. LCA 테이블 갱신

```
for (k=1..LOG)
parent[k][v]=parent[k-1][parent[k-1][v] ]
```

새 노드가 추가될 때 조상 정보를 즉시 갱신하여 LCA를 빠르게 계산할 수 있도록 합니다.

---

# 4. LCA(a, b)

두 노드의 최소 공통 조상을 구합니다.

1.  깊이 맞추기

```java
for(int k=LOG-1;k>=0;k--) {
			if(depth[a]-(1<<k)>=depth[b]) {
				a = parent[k][a];
			}
		}
```

더 깊은 노드를 위로 올림

2.  두 노드를 같이 올리면서 LCA 바로 아래까지 이동

```java
for(int k=LOG-1;k>=0;k--) {
			if(parent[k][a] != parent[k][b]) {
				a = parent[k][a];
				b = parent[k][b];
			}
		}
```

3. 부모 반환

```java
returnparent[0][a]
```

Binary Lifting을 이용하여 LCA를 **O(log N)**에 계산합니다.

---

# 5. measure(mDevice1, mDevice2)

두 장비 사이의 전송 시간을 반환합니다.

1. LCA 계산

```java
intc = LCA(mDevice1,mDevice2)
```

2. 거리 공식 적용

```java
dist = distRoot[a]+distRoot[b]-2*distRoot[c]
```

루트 기준 거리 정보를 이용하여 **두 노드 거리 계산을 빠르게 처리**합니다.

---

# 6. MaxDist(start, ban, initialWeight)

특정 방향으로 갔을 때 최대 거리를 구하는 함수입니다.

```
start : 탐색 시작 노드
ban   : 되돌아가지 않도록 막는 노드
initialWeight : 시작 간선 거리
```

DFS를 이용하여 해당 방향으로 갈 수 있는 최대 거리를 계산합니다.

```
if (nxt==parent)continue;
```

트리는 양방향 그래프이므로 부모 방향으로 되돌아가는 것을 방지합니다.

---

# 7. test(mDevice)

해당 장비를 반드시 지나는 최대 전송 시간을 계산합니다.

트리 성질 상 `x를 지나는 경로 =x 기준 두 방향으로 뻗는 경로`형태가 됩니다.

그래서 각 방향 최대 거리를 계산하고 가장 큰 값 + 두 번째 값을 반환합니다.

```java
for (Edge e :graph[x]) {
		long dist = MaxDist(e.to, x, e.w);

			if (dist>best1) {
				best2 = best1;
				best1 = dist;
			}else if (dist>best2) {
				best2 = dist;
			}
}
return (int)(best1 + best2);
```

---

# 시간 복잡도

| 함수    | 시간     |
| ------- | -------- |
| connect | O(log N) |
| measure | O(log N) |
| test    | O(N)     |

---
