package 국가행정;

import java.util.PriorityQueue;

/*
 * 메모리: 172,700 kb 실행시간: 679ms
 */

class UserSolution
{
	static class City implements Comparable<City>{
		int idx,population, road, weight;

		

		public City(int idx, int population, int road, int weight) {
			super();
			this.idx = idx;
			this.population = population;
			this.road = road;
			this.weight = weight;
		}



		@Override
		public int compareTo(City o) {
			if (this.weight != o.weight) {
		        return Integer.compare(o.weight, this.weight); // weight 큰 게 먼저
		    }
		    return Integer.compare(this.idx, o.idx); // idx 작은 게 먼저
		}
		
		
	}
	
	static City city[];
	static PriorityQueue<City> pq;
	static long prefix[];
	static int maxPopulation;
	
	void updateWeight(int N) {
		city[N].weight = (city[N].population+city[N+1].population)/city[N].road;
	}
	
	void init(int N, int mPopulation[])
	{
		city = new City[N];
		prefix = new long[N];
		pq = new PriorityQueue<>();
		maxPopulation = 0;
		
		for(int i=0;i<N;i++) {
			city[i] = new City(i, mPopulation[i], 1, 0);
			maxPopulation = Math.max(maxPopulation, city[i].population);
			if(i==0) prefix[i] = city[i].population;
			else prefix[i] = prefix[i-1] + city[i].population;
		}
		for(int i=0;i<N-1;i++) {
			updateWeight(i);
			pq.add(city[i]);
		}
		
		return;
	}

	int expand(int M)
	{
		int last=0;
		for(int i=0;i<M;i++) {
			City cur = pq.poll();
			cur.road += 1;
			updateWeight(cur.idx);
			last = cur.idx;  
			pq.add(cur);
		}
		
		return city[last].weight;
	}
	
	int calculate(int mFrom, int mTo)
	{
		if(mFrom>mTo) {
			int temp = mFrom;
			mFrom = mTo;
			mTo = temp;
		}
		int time = 0;
		for(int i=mFrom;i<mTo;i++) {
			time += city[i].weight;
		}
		return time;
	}
	
	
	int divide(int mFrom, int mTo, int K)
	{
		//mFrom~mTo에서 K-1개를 선택하는 조합 => 절대안됨
		//1.이분탐색으로 인구수 지정
		//2.인구수로 분할할 수 있는지
		long left = maxPopulation;
		long right = prefix[mTo]-(mFrom>0?prefix[mFrom-1]:0);
		long minP = Integer.MAX_VALUE;
		
		while(left<=right) {
			long mid = (left+right)/2;
			int cnt = 0;
			int split = 0;
			boolean possible = true;
			for(int i=mFrom;i<=mTo;i++) {
				cnt+=city[i].population;
				if(cnt>mid) {
					split ++;
					cnt = city[i].population;
				}
				//K를 넘으면 분할 불가
				if(split>=K) {
					possible = false;
					break; 
				}
			}
			if(possible) { //분할 가능하면 인구수 줄이기
				minP = mid;
				right = mid-1;
			}else {
				left = mid+1;
			}
		}
		
		return (int)minP;
	}
}