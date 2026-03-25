## 풀이 전략

`M`번 도시와 `M+1`번 도시 사이에 A개의 다리를 놓을 수 있음<br>
두 도시를 오가는 시간은 다음과 같음

-  `P(M)`이 M번 도시의 인구 수라고 할 때
-  `(P(M) + P(N+1)) / A`

도시를 오가는 시간은 인구 수와 다리의 수 모두에 영향을 받음.

- 두 도시 사이에 놓인 다리의 수도 같이 캐싱해야함
- 두 도시 사이의 구간에 대한 다리의 수, 횡단 시간을 저장하는 객체가 필요

추가로 다리를 건설할 때 가장 시간이 오래 걸리는 구간에 건설함

- 구간마다 우선순위가 존재
- 구간을 모두 PQ에 넣어야 겠음

## 구현

### 자료구조

```java
static class Bridge implements Comparable<Bridge>{
  int index, count, time;
  Bridge(int index, int count, int dist) {
    this.index = index;
    this.count = count;
    this.time = dist;
  }
  
  @Override
  public int compareTo(Bridge o) {
    if(this.time == o.time) {
      return this.index - o.index;
    }
    return o.time - this.time;
  }
}
```

이름을 Bridge라고 했지만 두 도시 사이 구간에 대한 객체임

- index : 구간의 번호 - `A`번 구간은 `A`번 도시와 `A+1`번 도시의 사이 구간임
- count : 구간에 놓인 다리 수
- time : 구간을 횡단하는데 걸리는 시간

PQ에 넣어서 다리 건설의 우선순위를 정해야 해서 Comparable을 상속받음

```java
Bridge bridges[] = new Bridge[MAX_CITY_COUNT-1];
```

각 구간에 대한 객체를 저장할 배열.

문제에서의 최대 크기로 선언해둠

셀에 객체를 생성했다면 이를 재활용해서 GC를 줄임

```java
PriorityQueue<Bridge> pq = new PriorityQueue<>(); 
```

다리 건설 우선순위를 구하는데 사용할 PQ

`Bridges`에 저장된 객체를 삽입해서 GC를 줄임


### init

모든 자료 구조를 초기화

- `mPopulation` : 파라미터로 전달받은 `mPopulation`로 초기함
- `bridges` : 
  - cell이 null인 경우 객체 생성 후 초기화
  - 객체가 존재할 경우 변수만 초기화
- `pq` : 한 번 비운 후 사용 범위 내 `bridges[]`를 삽입

### expand

1. `pq`에서 `pop`한 데이터를 갱신할 후 다시 `push`
2. 1을 `M`번 반복

`O(K*logN)`

### calculate

`bridges[mFrom]`부터 `bridges[mTo]`까지 구간의 횡단 시간을 모두 더함

`O(N)`

### divide

이 부분은 몰라서 Gemini한테 물어봄

결론은 파라메트릭 탐색을 사용함

방법은 이진 탐색과 값의 일치를 찾는 반면

파라메트릭 탐색은 값이 최적적의 해인지 구함

그래서 우선 해인지 판별하기 위한 `canDivide` 함수를 구현함

`canDivide`가 `true`를 반환한 값들 중 최솟값을 최적해로 반환함

### canDivide

이 함수는 최적의 조합을 찾는 함수가 아닌

최적을 해를 만들 수 있는지를 확인하는 함수임

그래서 선거권을 K개로 나누지 않더라도

임의의 선거권 최대 인구수를 M이라 할 때, 

선거권을 K개 이하로 나눴을 때 모든 선거권의 인구수가 M명 이하인지 확인함