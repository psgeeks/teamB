import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
/*
 * 메모리: 36,268 kb
 * 실행시간: 524 ms
 * 
 * @param Node 메모리 할당된 영역을 start, end로 저장
 * @param list 메모리 주소 순으로 할당한 값들을 순회할 수 있도록 만듦
 * @param nodeMap 해당 변수가 할당된 메모리 주소 매핑, 리스트에서 노드를 O(1)의 속도로 제거하기 위한 map 
 * 
 * # malloc
 * 1. 리스트를 순회하며 빈 칸이 size 이상인지 확인한다.
 * 2. size가 확보되면 해당 빈 칸의 시작점을 map의 value로 저장
 * 3. size가 없다면 0을 map의 value로 저장한다.
 * 
 * # free
 * 1. 해당 변수에 메모리가 할당되었는지 확인한다.
 * 2. 할당되어있다면 map으로 접근하여 list에서 해당 노드를 제거
 * 3. map에서 제거
 * 
 * # print
 * 1. 해당 변수에 메모리가 할당되었는지 확인한다.
 * 2. 할당되어있다면 map에서 해당 변수의 시작주소 출력.
 * 3. 아니라면 0 출력 
 * 
 */
public class Main {
	static LinkedList list = new LinkedList();
	static Map<String ,Node> nodeMap = new HashMap<>(); 
	static final int MAX_MEMORY = 100_000;
	
	static class Node {
		int start, end;
		Node prev, next;
		
		public Node(int s, int e) {
			start = s;
			end = e;
		}
		
		public String toString() {
			return "["+start+ ",  " + end +"]  ";
		}
	}
	
	static class LinkedList{
		Node head, tail;
		
		public LinkedList() {
			head = new Node(-1,-1);
			tail = new Node(MAX_MEMORY+1,MAX_MEMORY+1);
			
			head.next = tail;
			tail.prev = head;
		}
	}
	
	static String getVar(String command) {
		int idx = command.indexOf('=');
		return command.substring(0,idx);
	}
	
	static int getSize(String command) {
		int l = command.indexOf('(')+1;
		int r = command.indexOf(')');
		
		return Integer.parseInt(command.substring(l,r));
	}
	
	static String getTarget(String command) {
		int l = command.indexOf('(')+1;
		int r = command.indexOf(')');
		
		return command.substring(l,r);
	}
	
	static void addNode(String var, int size) {
		int cur = 1;
		Node nextNode = list.head.next;
		boolean isAllocated = false;
		
		while (cur + size - 1 <= MAX_MEMORY) {
			if (nextNode.start - cur < size) {
				if (nextNode == list.tail) break;
				
				cur = nextNode.end + 1;
				nextNode = nextNode.next;
				continue;
			}
			
			Node n = new Node(cur, cur+size-1);
			n.prev = nextNode.prev;
			n.next = nextNode;
			nextNode.prev.next = n;
			nextNode.prev = n;
			
			nodeMap.put(var, n);
			
			isAllocated = true;
			break;
		}
		if (!isAllocated) {
			nodeMap.put(var, new Node(0,0));
		}
	}
	
	static void removeNode(String target) {
		if (nodeMap.get(target)==null || nodeMap.get(target).start==0) return;
		
		Node n = nodeMap.get(target);
		n.prev.next = n.next;
		n.next.prev = n.prev;
		nodeMap.remove(target);
	}
	
	public static void main(String[] args) throws IOException {
		StringBuilder output = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int N = Integer.parseInt(br.readLine());
		
		for (int i=0; i<N; i++) {
			String command = br.readLine();
			
			// 1. free
			if (command.startsWith("free(")) {
				String target = getTarget(command);
				if (nodeMap.get(target)==null || nodeMap.get(target).start==0) continue;
				
				removeNode(target);
			}
			// 2. print
			else if (command.startsWith("print(")) {
				String target = getTarget(command);
				
				if (nodeMap.get(target)==null || nodeMap.get(target).start==0) {
					output.append(0).append("\n");
					continue;
				} else {
					output.append(nodeMap.get(target).start).append("\n");
				}
			}
			// 3. malloc
			else {
				String var = getVar(command);
				int size = getSize(command);
				
				addNode(var, size);
			}
			
		}
		
		System.out.println(output);
		br.close();
	}
	
	static void dbg(Object...objects) {
		System.out.println(Arrays.deepToString(objects));
	}
}
