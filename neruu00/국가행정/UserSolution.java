package 국가행정;

import java.util.PriorityQueue;

class UserSolution {
	static final int MAX_CITY_COUNT = 10000;

	static class Bridge implements Comparable<Bridge> {
		/**
		 * index : 다리가 놓인 위치
		 * - index == X : X번 도시와 X+1번 도시 사이에 놓인 다리들
		 * count : 다리가 놓인 수
		 * time : 횡단 시간
		 */
		int index, count, time;

		Bridge(int index, int count, int dist) {
			this.index = index;
			this.count = count;
			this.time = dist;
		}

		@Override
		public int compareTo(Bridge o) {
			if (this.time == o.time) {
				return this.index - o.index;
			}
			return o.time - this.time;
		}
	}

	// 도시의 인구 수를 저장할 배열
	int mPopulation[];

	// 다리 건설 위치 우선순위
	PriorityQueue<Bridge> pq = new PriorityQueue<>();

	// 다리 정보 객체 배열
	Bridge bridges[] = new Bridge[MAX_CITY_COUNT - 1];

	void init(int N, int mPopulation[]) {
		this.mPopulation = mPopulation;

		pq.clear(); // pq 초기화

		for (int i = 0; i < N - 1; i++) {
			// 빈 셀인 경우 객체 생성
			if (bridges[i] == null) {
				bridges[i] = new Bridge(i, 1, mPopulation[i] + mPopulation[i + 1]);
			}
			// 객체가 있을 경우 값만 초기화
			else {
				bridges[i].count = 1;
				bridges[i].time = mPopulation[i] + mPopulation[i + 1];
			}
			// 생성된 객체를 pq에 삽입
			pq.offer(bridges[i]);
		}
	}

	int expand(int M) {
		// 마지막에 반환할 거리를 저장할 변수
		int tmp = 0;
		for (int i = 0; i < M; i++) {
			// 우선순위가 높은 다리 객체
			Bridge bridge = pq.poll();

			int index = bridge.index;

			// 다리 개수 추가 후 횡단 거리 계산
			tmp = (mPopulation[index] + mPopulation[index + 1]) / ++bridge.count;

			bridge.time = tmp;

			// 횡단 시간 갱신 후 pq에 다시 다리 객체 삽입
			pq.offer(bridge);
		}
		return tmp;
	}

	int calculate(int mFrom, int mTo) {
		int sum = 0;
		int from, to;

		// from < to가 성립하도록 인덱스 스왑
		if (mFrom < mTo) {
			from = mFrom;
			to = mTo;
		} else {
			to = mFrom;
			from = mTo;
		}

		// mFrom 도시부터 mTo까지 걸리는 시간 계산
		for (int i = from; i < to; i++) {
			sum += bridges[i].time;
		}
		return sum;
	}

	/**
	 * 선거권을 K개 이하로 나눴을 때 모든 선거권의 인구수가 mid명 이하인지 확인함
	 * 
	 * @param mTo - 뒤 도시 번호
	 * @param K   - 선거구 수
	 * @param mid - 최대 인구 수
	 */
	boolean canDivide(int mFrom, int mTo, int K, int mid) {

		int count = 1; // 필요한 선거구 수
		int sum = 0; // count번째 선거구의 인구 수

		for (int i = mFrom; i <= mTo; i++) {
			if (sum + mPopulation[i] <= mid) {
				sum += mPopulation[i];
			} else {
				if (++count > K)
					return false;
				sum = mPopulation[i];
			}
		}
		return true;
	}

	int divide(int mFrom, int mTo, int K) {
		// 단일 도시 중 가장 많은 인구 수
		// 나올 수 있는 선거구 인구 수 최솟값
		int left = 0;
		// 범위 내 모든 인구 수의 합
		// 나올 수 있는 선거구 인구 수 초
		int right = 0;

		for (int i = mFrom; i <= mTo; i++) {
			left = Math.max(left, mPopulation[i]);
			right += mPopulation[i];
		}

		int ans = 0;
		int mid = 0;

		// 파다메트릭 서치
		// 이분탐색과 같은 방법으로 나올 수 있는 최소값을 구함
		while (left <= right) {
			mid = (left + right) / 2;
			if (canDivide(mFrom, mTo, K, mid)) {
				ans = mid;
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		return ans;
	}
}