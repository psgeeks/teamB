package 광물운송;

class UserSolution {
	int L, N, size;

	int r[] = new int[20000]; // row
	int c[] = new int[20000]; // col
	int q[] = new int[20000]; // quantity
	int id[] = new int[20000]; // id

	int parent[] = new int[20000];
	int sum[] = new int[20000]; // 서로소 집합 내 채굴량의 합

	/**
	 * 베이스캠프 우선순위 반환 함수
	 * @return
	 * - true -> a가 우선순위 높음 <br>
	 * - false -> b가 우선순위 높음
	 */
	boolean isBetter(int a, int b) {
		if (q[a] != q[b]) return q[a] < q[b];
		if (r[a] != r[b]) return r[a] < r[b];
		return c[a] < c[b];
	}

	// 탐색 속도와 스택오버플로우 방지를 위해 반복문으로 find
	int findSet(int a) {
		int root = a;
		while (root != parent[root]) {
			root = parent[root];
		}
		return root;
	}

	// 두 집합을 합치는 함수
	// 우선순위가 높은 root에 우선순위가 낮은 root를 붙인다
	void union(int a, int b) {
		int rootA = findSet(a);
		int rootB = findSet(b);

		if (rootA == rootB)
			return;

		if (isBetter(rootA, rootB)) {
			parent[rootB] = rootA;
			sum[rootA] += sum[rootB];
		} else {
			parent[rootA] = rootB;
			sum[rootB] += sum[rootA];
		}
	}

	void init(int L, int N) {
		this.L = L;
		this.N = N;
		size = 0;
	}

	int addBaseCamp(int mID, int mRow, int mCol, int mQuantity) {
		int u = size++;
		id[u] = mID;
		r[u] = mRow;
		c[u] = mCol;
		q[u] = mQuantity;
		parent[u] = u;
		sum[u] = mQuantity;

		// 두 베이스캠프를 비교해서 이동 가능한 거리에 있다면 union
		for (int i = 0; i < u; i++) {
			if (Math.abs(r[i] - r[u]) + Math.abs(c[i] - c[u]) > L) continue;
			union(i, u);
		}

		return sum[findSet(u)];
	}

	int findBaseCampForDropping(int K) {
		int ans = -1;

		for (int i = 0; i < size; i++) {
			// 자신이 루트가 아니거나, 집합의 채굴량 합이 K보다 작은 경우
			if (parent[i] != i || sum[i] < K) continue;
			// 비교한 ans가 없거나 우선순위가 높다면 ans 갱신
			if (ans == -1 || isBetter(i, ans)) ans = i;
		}
		return ans == -1 ? -1 : id[ans];
	}
}
