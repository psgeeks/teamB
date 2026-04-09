---

# 사용한 자료구조

## 1. 도로 정보 클래스 및 배열

```java
Road[] roads
int[] popul
```

각 도시의 인구수와 도로 정보를 저장합니다.

**Road**

- `from` : 도로의 시작 도시 번호
- `weight` : 도로의 초기 가중치 (연결된 두 도시의 인구수 합)
- `num` : 해당 도로의 차선 수
- `time` : 도로 이동 시간 (가중치 / 차선 수)

---

## 2. 우선순위 큐 (도로 선택)

```java
PriorityQueue<Road> pq
```

확장할 도로를 선택하기 위해 사용합니다.

- 기준: 이동 시간(`time`)이 가장 긴 도로 우선
- tie-break: 시작 도시 번호(`from`)가 작은 도로 우선

```java
@Override
public int compareTo(Road o) {
    if(this.time != o.time) return Integer.compare(o.time, this.time);
    return Integer.compare(this.from, o.from);
}
```

---

## 3. 인구 정보 배열

```java
int[] popul
```

각 도시의 인구 정보를 저장하고 구간합을 계산할 때 참조합니다. (누적합 배열을 별도로 만들지 않고, 필요할 때마다 반복문으로 합산하는 방식을 사용했습니다.)

- 특정 도시 인구: `popul[i]`
- 구간합 계산: `for`문을 이용해 `popul[i]` 누적 합산

---

# 함수별 정리

## 1. init(N, mPopulation)

초기 도시 인구 및 도로 상태를 설정하는 함수입니다.

- 인구 정보 배열 저장
- 도로 배열 생성
- 초기 도로 `weight` 계산 및 `Road` 객체 생성
- 우선순위 큐에 모든 도로 삽입

```java
roads[i] = new Road(i, popul[i] + popul[i+1]);
pq.add(roads[i]);
```

---

## 2. updateRoad()

도로의 이동 시간(`time`)을 갱신하는 내부 메서드입니다.

```java
time = (int)(weight/++num);
```

- 도로의 초기 가중치(`weight`)를 사용
- 차선 수(`num`)를 1 증가시킨 후 나눔 (정수 나눗셈 처리)

---

## 3. expand(M)

도로 확장을 M번 수행합니다.

1. 가장 소요 시간이 긴 도로 선택 (PQ)
2. 차선 수 증가 및 소요 시간 갱신 (`updateRoad`)
3. 갱신된 도로를 다시 PQ에 삽입

```java
exRoad = pq.poll();
exRoad.updateRoad();
pq.add(exRoad);
```

---

## 4. calculate(mFrom, mTo)

두 도시 사이의 총 이동 시간을 계산합니다.

```java
if(mFrom > mTo) {
    // 위치 swap 로직
}
```

방향을 항상 오름차순으로 정렬한 뒤 순회하며 계산합니다.

```java
for(int i = mFrom; i < mTo ; i++) {
    sumTime += roads[i].time;
}
```

---

## 5. divide(mFrom, mTo, K)

해당 구간을 K개 이하의 선거구로 나눌 때 **한 그룹의 최대 인구를 최소화**합니다.

### 1. 문제 변환

단순 조합으로는 시간 내에 풀 수 없으므로,
"한 그룹의 최대 인구 한도를 `limit`으로 했을 때 `K`개 이하로 나눌 수 있는가?"라는 결정 문제로 변환하여 해결합니다.

### 2. 이분탐색 (Parametric Search)

```text
start = 0
end = 구간 내 전체 인구 합
```

### 3. 판정 (그리디 - canDiv)

왼쪽부터 인구를 누적 합산(`pSum`)합니다. 현재 인구를 더했을 때 한도(`limit`)를 넘으면 그룹을 분할합니다.

```java
if(pSum + popul[i] <= limit) {
    pSum += popul[i];
} else {
    cnt++;
    pSum = popul[i];
}
```

### 4. cnt 의미

- `cnt` = 나누어진 총 그룹의 수
- 조건: `if(cnt > K) return false;` → 분할된 그룹 수가 K개를 초과하면 현재 `limit`으로는 불가능함을 의미

### 5. 이분탐색 진행

```java
if(canDiv(mFrom, mTo, K, mid)) {
    result = mid;
    end = mid - 1; // 가능하므로 더 작은 최댓값을 탐색
} else {
    start = mid + 1; // 불가능하므로 한도를 늘려서 탐색
}
```

---

# 시간 복잡도

- **init**: $O(N \log N)$ (N개의 도로를 우선순위 큐에 삽입)
- **expand**: $O(M \log N)$ (우선순위 큐 추출 및 삽입 M번 반복)
- **calculate**: $O(N)$ (최대 N개의 도로 시간 합산)
- **divide**: $O(N \log S)$ ($S$는 구간 내 인구의 총합이며, 이분 탐색의 각 단계마다 $O(N)$의 `canDiv` 함수 수행)

---

# 배운 점

- 연속 구간 분할 문제를 이분탐색 + 그리디로 해결하는 방법을 배웠습니다.
- parametric search 를 적용하는 사고력을 길러야겠다고 생각하였고, 구현법 또한 완벽히 익숙하지는 않아서 더 풀어봐야할 것 같습니다.

---