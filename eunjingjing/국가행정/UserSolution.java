package 국가행정;

import java.util.PriorityQueue;

class UserSolution
{
	class Road implements Comparable<Road>{
		int from, weight, num, time;

		public Road(int from, int weight) {
			super();
			this.from = from;
			this.weight = weight;
			this.num = 1;
			this.time = weight;
		}
		
		@Override
		public int compareTo(Road o) {
			if(this.time != o.time) return Integer.compare(o.time, this.time);
			return Integer.compare(this.from, o.from);
		}
		
		void updateRoad() {
			time = (int)(weight/++num);
		}
	}
	
	int N, popul[];
	Road roads[];
	PriorityQueue<Road> pq;
	
	void init(int N, int mPopulation[])
	{
		this.N = N;
		popul = mPopulation	;
		roads = new Road[N-1];
		pq = new PriorityQueue<>();
		for(int i = 0; i < N-1; i++) {
			roads[i] = new Road(i, popul[i] + popul[i+1]);
			pq.add(roads[i]);
		}
		return;
	}

	int expand(int M)
	{
		Road exRoad = null;
		while(M-- > 0) {
			exRoad = pq.poll();
			exRoad.updateRoad();
			pq.add(exRoad);
		}
		return exRoad.time;
	}
	
	int calculate(int mFrom, int mTo)
	{
		if(mFrom > mTo) {
			int temp = mFrom;
			mFrom = mTo;
			mTo = temp;
		}
		
		int sumTime = 0;
		for(int i = mFrom; i < mTo ; i++) {
			sumTime += roads[i].time;
		}
		
		return sumTime;
	}
	
	int divide(int mFrom, int mTo, int K)
	{
		int start = 0;
		int end = 0;
		
		for(int i = mFrom; i <= mTo; i++) {
			end += popul[i];
		}
		
		int mid, result = 0;
		while(start <= end) {
			mid = start + (end - start)/2;
			if(canDiv(mFrom, mTo, K, mid)) {
				result = mid;
				end = mid - 1;
			} else {
				start = mid + 1;
			}
		}
		
		return result;
	}
	
	boolean canDiv(int mFrom, int mTo, int K, int limit) {
		int cnt = 1, pSum = 0;
		for(int i = mFrom; i <= mTo; i++) {
			if(pSum + popul[i] <= limit) {
				pSum += popul[i];
			} else {
				cnt++;
				pSum = popul[i];
				if(cnt > K) return false;
			}
		}
		
		return true;
	}
}