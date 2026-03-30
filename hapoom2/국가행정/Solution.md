# 사용한 자료구조

## 1. 도시 정보 배열

```
City[] city
```

각 도시와 도로 정보를 저장합니다.

**City**

- `idx` : 도시 번호
- `population` : 도시 인구
- `road` : 해당 도로의 차선 수
- `weight` : 도로 이동 시간

---

## 2. 우선순위 큐 (도로 선택)

```
PriorityQueue<City>pq
```

확장할 도로를 선택하기 위해 사용합니다.

- 기준: 이동 시간이 가장 긴 도로 우선
- tie-break: idx가 작은 도로 우선

```java
if (this.weight!=o.weight) return Integer.compare(o.weight,this.weight);
return Integer.compare(this.idx,o.idx);
```

---

## 3. 누적합 배열

```java
long[] prefix
```

도시 인구의 구간합을 빠르게 계산하기 위해 사용합니다.

- 0~i까지 인구 합: `prefix[i]`
- 구간합 계산: `sum(mFrom,mTo)=prefix[mTo]-prefix[mFrom-1]`

---

# 함수별 정리

## 1. init(N, mPopulation)

초기 도시 및 도로 상태를 설정하는 함수입니다.

- 도시 배열 생성
- 인구 정보 저장
- 누적합 배열 생성
- 초기 도로 weight 계산
- 우선순위 큐에 도로 삽입

```java
city[i] = newCity(i,mPopulation[i],1,0);
updateWeight(i);
pq.add(city[i]);
```

---

## 2. updateWeight(N)

도로의 이동 시간을 계산하는 함수입니다.

```java
(city[N].population+city[N+1].population)/city[N].road
```

- 두 도시 인구 합
- 차선 수로 나눔 (정수 나눗셈 → 버림)

---

## 3. expand(M)

도로 확장을 M번 수행합니다.

1. 가장 weight가 큰 도로 선택 (PQ)
2. 차선 수 증가
3. weight 갱신
4. 다시 PQ에 삽입

```java
Citycur = pq.poll();
cur.road++;
updateWeight(cur.idx);
pq.add(cur);
```

---

## 4. calculate(mFrom, mTo)

두 도시 사이 이동 시간을 계산합니다.

```java
if (mFrom>mTo) swap
```

방향 정렬 후 계산

```java
for (i=mFrom ; i<mTo ; i++)
time += city[i].weight;
```

도로 개수 = `mTo - mFrom`

---

## 5. divide(mFrom, mTo, K) ⭐

해당 구간을 K개의 선거구로 나눌 때 **최대 인구를 최소화**

### 1. 문제 변환

조합으로는 절대 풀 수 없기 때문에

직접 자르는 것이 아니라 “최대 구간 인구를 X로 했을 때 나눌 수 있는가?”로 바꿔서 해결

### 2. 이분탐색 (Parametric Search)

```
left = 구간 내 최대 인구
right = 구간 전체 인구 합
```

### 3. 판정 (그리디)

왼쪽부터 누적하면서

```java
cnt += population
```

인구가 mid를 넘으면 분할

```java
if (cnt>mid) {
	split++;
	cnt= 현재 도시 인구;
}
```

### 4. split 의미

- `split` = 자른 횟수
- 실제 구간 수 = `split + 1`
- 조건: `if (split>=K)` → K개 초과 구간 발생하므로 불가능

### 5. 이분탐색 진행

```
if (가능) → right=mid-1
else      → left=mid+1
```

---

# 시간 복잡도

---

# ✨ 배운 점

- 연속 구간 분할 문제를 이분탐색 + 그리디로 해결하는 방법을 배웠습니다.
- 문제를 직접 해결하려 하지 않고 판정 문제로 바꾸는 사고 방식이 중요하다는 것을 느꼈습니다.
- 우선순위 큐에서 tie-break 조건을 정확히 처리하는 것이 중요하다는 것을 배웠습니다.
- 누적합을 활용하면 구간합을 효율적으로 처리할 수 있습니다.