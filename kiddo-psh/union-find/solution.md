아래처럼 다듬으면 깔끔합니다.

## 제약사항

* `init(L, N)`

  * `3 <= L <= 500`
  * `9 <= N <= L * 30`

* `addBaseCamp(...)`

  * 호출 최대 `20,000`
  * `mID <= 10^9`
  * `mQuantity <= 100`

* `findBaseCampForDropping(K)`

  * 호출 최대 `500`

## 자료구조 설계

* `Map<Integer, Integer> idToIdx`

  * 캠프 ID를 Union-Find용 인덱스로 매핑

* `int[] parent, rank, quantity`

  * Union-Find 관리용 배열
  * `quantity[root]` 에 컴포넌트의 총 채굴량 저장

* `Camp`

  * `id, row, col, quantity` 저장

* `List<Camp>[][] grid`

  * 좌표 범위를 직접 다루지 않고, `L` 단위 블록으로 나누어 저장
  * 새 캠프 추가 시 주변 블록만 확인하여 인접 캠프 탐색

* `TreeSet<Camp>`

  * 채굴량, 행, 열 우선순위로 전체 캠프 정렬
  * `find` 에서 조건을 만족하는 캠프를 빠르게 선택

## 한줄평

Union-Find로 연결 컴포넌트를 관리하고, 격자 압축으로 인접 후보를 줄인 뒤, 정렬된 캠프 집합으로 조건에 맞는 시작점을 찾는 문제였다.
