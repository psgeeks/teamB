### 자료 구조

```java
static class Edge {
    int to, weight;
    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}
```

많이 보던 간선을 저장하기 위한 클래스 사용

```java
  Map<Integer, Integer> deviceMap;
```

`mDevice`와 트리의 인덱스를 매칭해주는 `HashMap`

놀랍게도 `HashMap`의 탐색 속도는 O(1)이라고 합니다 **ㄴㅇㄱ (?!)**

```java
  List<List<Edge>> tree;
```

정점과 연결된 간선을 저장하기 위한 리스트

`tree[정점][간선]` 이런 식으로 저장됨

### init()

모든 자료 구조를 초기화 함

그리고 트리에 시작 정점 하나를 삽입하고

삽입한 정점에 해당하는 리스트를 트리에 하나 생성

리스트에는 정점에 연결된 간선을 저장할 거임

### connect()

트리에 새로운 정점 newDevice를 삽입함

그래고 oldDevice와 newDevice의 간선에 대한 정보를 각 정점의 리스트에  삽입함

### measure()

시작과 종료 정점에 해당하는 인덱스를 찾은 다음 두 정점 사이의 거리를 계산함

시작 정점을 기준으로 BFS를 활용해 종료 정점까지의 최단 거리를 계산함

### test()

mDevice에서 가장 먼 정점 V<sub>1</sub>, V<sub>2</sub>에 대해 mDevice->N<sub>1</sub>거리와 mDevice->N<sub>2</sub>의 합을 구한다.

단, mDevice->N<sub>1</sub>거리와 mDevice->N<sub>2</sub>거리는 mDevice에 연결된 동일한 간선을 지나서는 안된다.

mDevice -> N의 거리는 계산할 때 아래 방법을 사용한다.

- 간선을 지나는 방향을 통제하기 위해 mDevice를 방문 표시함
- 탐색의 시작은 mDevice가 아닌 mDevice에 연결된 다음 정점임 V에서 시작함
- V->N의 거리를 계산한 후 mDevice->V의 거리를 더 한다.

이렇게 계산된 거리 중 가장 먼 거리 2개의 합을 반환한다.