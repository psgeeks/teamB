# SWEA UserSolution 풀이 정리

## 문제 구조 요약

- `init` : 초기화
- `expand` : 간선(도로) 확장 M회 수행, 마지막으로 확장된 구역의 이동시간 반환
- `calculate` : 특정 구간의 총 이동시간 합산
- `divide` : 특정 구간을 K개 이하의 선거구로 나눌 때, 선거구 최대 인구의 최솟값 반환

---

## 풀이 흐름

### expand — Priority Queue

expand에서 M번 간선을 확장할 때, **어떤 구간을 확장하는 것이 가장 이득인지** 매번 효율적으로 선택해야 한다.

처음부터 **우선순위 큐(Priority Queue)** 를 사용하는 것을 떠올렸다.

- 각 간선의 현재 이동시간 `(population[i] + population[i+1]) / edge수` 를 기준으로 내림차순 정렬
- 이동시간이 같으면 인덱스 오름차순으로 정렬
- 매 확장마다 큐에서 가장 이동시간이 큰 간선을 꺼내 간선 수를 +1하고, 새 이동시간으로 다시 삽입
```java
PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2)->{
    if(o1[0] != o2[0]) return o2[0] - o1[0];
    return o1[1] - o2[1];
});
```

---

### calculate — 펜윅 트리 (Fenwick Tree / BIT)

구간 합을 구하는 `calculate`는 처음에 선형으로 순회하며 더하는 방법을 생각했다.

그런데 **B형 준비를 하면서 펜윅 트리(Fenwick Tree)** 를 공부한 것을 떠올려 이번 기회에 적용해 보았다. (세그먼트 트리 대신 쓸 구조로 벼락치기 함)

#### 펜윅 트리란?

> 구간 합을 **O(log N)** 에 구하고, 값 업데이트도 **O(log N)** 에 처리할 수 있는 자료구조.  
> 세그먼트 트리보다 **구현이 훨씬 단순**

- **update** : 특정 인덱스의 값을 변경할 때, 해당 인덱스에 영향을 받는 상위 노드들을 갱신
- **sum** : 1번 인덱스부터 특정 인덱스까지의 누적 합을 반환
```java
void update(int idx, int diff) {
    while(idx <= size) {
        tree[idx] += diff;
        idx += (-idx & idx); // 최하위 비트만큼 이동
    }
}

int sum(int idx) {
    int result = 0;
    while(idx > 0) {
        result += tree[idx];
        idx -= (-idx & idx); // 최하위 비트만큼 이동
    }
    return result;
}
```

`calculate(mFrom, mTo)` 는 아래와 같이 구간 합을 구한다.
```java
return myFenwick.sum(mTo) - myFenwick.sum(mFrom);
```

`expand`에서 간선의 이동시간이 바뀔 때마다 펜윅 트리도 함께 업데이트하여 항상 최신 상태를 유지한다.

---

### divide — 파라메트릭 서치 (Parametric Search)

선거구 최대 인구의 최솟값을 구하는 문제로, **이진 탐색 방향**으로 접근하는 것은 자연스럽게 떠올렸다.

다만 **파라메트릭 서치(Parametric Search)** 의 구조를 정확히 알지 못해서 검색 후 구조를 파악하여 구현할 수 있었다.

#### 파라메트릭 서치란?

> "정답이 될 수 있는 값의 범위"에 이진 탐색을 적용하고,  
> 각 mid 값에 대해 **"이 값이 조건을 만족하는가?"** 를 판단하는 함수를 이용해 정답을 좁혀나가는 기법.

- **탐색 범위**
  - `start` : 구간 내 인구 최댓값 (선거구 최대 인구의 하한)
  - `end` : 구간 전체 인구 합 (선거구 최대 인구의 상한)

- **조건 판단 함수 `canDiv`**  
  선거구 최대 인구를 `limit`으로 제한할 때, 구간을 **K개 이하**로 나눌 수 있는지 그리디하게 검사
```java
boolean canDiv(int mFrom, int mTo, int K, int limit) {
    int cnt = 1;
    int cur = 0;
    for(int i = mFrom; i <= mTo; i++) {
        int curP = population[i];
        if(cur + curP > limit) {
            cnt++;
            cur = curP;
            if(K < cnt) return false;
        }
        else cur += curP;
    }
    return true;
}
```

- `canDiv`가 `true`이면 → 더 작은 값으로 좁히기 (`end = mid - 1`)
- `canDiv`가 `false`이면 → 더 큰 값으로 넓히기 (`start = mid + 1`)

---

## 성능 결과

| 항목 | 결과 |
|------|------|
| 메모리 | 174,844 KB |
| 실행시간 | 654 ms |

---

## 핵심 자료구조 및 알고리즘 요약

| 함수 | 사용 기법 | 시간 복잡도 |
|------|-----------|-------------|
| `expand` | Priority Queue | O(M log N) |
| `calculate` | Fenwick Tree (BIT) | O(log N) |
| `divide` | Parametric Search + Greedy | O(N log S) |