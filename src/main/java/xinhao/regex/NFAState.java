package xinhao.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author by xinhao  2021/8/8
 * 表示 NFA 转换图中的状态
 */
public class NFAState implements Comparable<NFAState> {

    // 表示 ε 空串
    public static final String EPSILON = "epsilon";
    private static int idGenerate = 0;
    // 标志是状态几
    private int id;
    // NFA 转换图中，当前状态通往下一个状态所有有向边
    private Map<String, Set<NFAState>> edges;
    // 表示当前状态是不是终止状态
    private boolean isEnd;

    public NFAState() {
    }

    // 创建状态节点
    public static NFAState create() {
        NFAState state = new NFAState();
        state.id = idGenerate++;
        state.edges = new HashMap<>();
        return state;
    }

    // 添加当前状态遇到输入字符 `path` ，进入的一个状态。就是其中一条有向边
    public void addEdge(String path, NFAState nextState) {
        Set<NFAState> set = edges.get(path);
        if (set == null) {
            set = new HashSet<>();
            edges.put(path, set);
        }
        set.add(nextState);
    }

    public Map<String, Set<NFAState>> getEdges() {
        return edges;
    }

    public int getId() {
        return id;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append("id=").append(id);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int compareTo(NFAState o) {
        return id - o.id;
    }
}
