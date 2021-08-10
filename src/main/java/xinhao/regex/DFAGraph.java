package xinhao.regex;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by xinhao  2021/8/8
 */
public class DFAGraph {
    public static final String[] PATHS = {"0","1","2","3","4","5","6","7","8","9",".","a", "b", "c"};

    public static final Map<String, DFAState> EMPTY = new HashMap<>();
    // DFA 开始状态节点
    private DFAState start;
    // DFA 对应的转换表。采用 Map<DFAState, Map<String, DFAState>> 表明它是一个二维数组，可以转换成 DFAState[][] 的格式
    private Map<DFAState, Map<String, DFAState>> stateTable;

    public DFAGraph(DFAState start) {
        this.start = start;
    }

    public static DFAGraph create(DFAState start) {
        DFAGraph dfaGraph = new DFAGraph(start);
        dfaGraph.stateTable = new HashMap<>();
        return dfaGraph;
    }

    /**
     * 向转换表中添加数据
     * @param currentState
     * @param path
     * @param state
     */
    public void addStateTable(DFAState currentState, String path, DFAState state) {
        Map<String, DFAState> pathMap = stateTable.get(currentState);
        if (pathMap == null) {
            pathMap = new HashMap<>();
            stateTable.put(currentState, pathMap);
        }
        pathMap.put(path, state);
    }

    /**
     * 获取对应的下一个状态节点
     * @param currentState
     * @param path
     * @return
     */
    public DFAState getStateByMove(DFAState currentState, String path) {
        return stateTable.getOrDefault(currentState, EMPTY).get(path);
    }

    public DFAState getStart() {
        return start;
    }

    public Map<DFAState, Map<String, DFAState>> getStateTable() {
        return stateTable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DFAGraph{");
        sb.append("start=").append(start);
        sb.append(", stateTable=").append(stateTable);
        sb.append('}');
        return sb.toString();
    }
}
