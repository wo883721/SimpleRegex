package xinhao.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * @author by xinhao  2021/8/10
 */
public class DFARegexUtil {

    public static final Set<NFAState> EMPTY = new HashSet<>();

    /**
     * 得到 DFA 状态节点 经过 path 后得到的所有 NFA 状态节点集合。
     * @param tState
     * @param path
     * @return
     */
    public static Set<NFAState> edge(DFAState tState, String path) {
        Set<NFAState> resultSet = new HashSet<>();
        // 当前 DFA 状态对应的 NFA 状态集合
        for (NFAState state : tState.getNFAStateSet()) {
            if (state.getEdges().containsKey(path)) {
                resultSet.addAll(state.getEdges().get(path));
            }
        }
        return resultSet;
    }

    /**
     * 得到 ε-closure 的 NFA 状态集合
     * @param state
     * @return
     */
    public static Set<NFAState> closure(NFAState state) {
        Set<NFAState> states = new HashSet<>();
        states.add(state);
        return closure(states);
    }

    /**
     * 得到 ε-closure 的 NFA 状态集合
     * @param states
     * @return
     */
    public static Set<NFAState> closure(Set<NFAState> states) {
        Stack<NFAState> stack = new Stack<>();
        stack.addAll(states);
        Set<NFAState> closureSet = new HashSet<>(states);
        while (!stack.isEmpty()) {
            NFAState state = stack.pop();
            // 得到状态 state 对应的空边 状态集合
            Set<NFAState> epsilonSet = state.getEdges().getOrDefault(NFAState.EPSILON, EMPTY);
            for (NFAState epsilonState : epsilonSet) {
                // 如果不存在，就是新发现的状态，添加到 closureSet 和 stack 中
                if (!closureSet.contains(epsilonState)) {
                    closureSet.add(epsilonState);
                    stack.push(epsilonState);
                }
            }
        }
        return closureSet;
    }

    /**
     * 从 dfaStates 集合中寻找一个未标记的 DFA 状态节点
     * @param dfaStates
     * @return
     */
    public static DFAState getNoTagState(Set<DFAState> dfaStates) {
        for (DFAState state : dfaStates) {
            if (!state.isTag()) {
                return state;
            }
        }
        return null;
    }

    /**
     * NFA 转换成 DFA
     * @param nfaGraph
     * @return
     */
    public static DFAGraph NFAToDFA(NFAGraph nfaGraph) {

        // 创建开始的 DFA 状态
        DFAState startDFAState = DFAState.create(closure(nfaGraph.getStartState()));
        // 创建 DFAGraph 图
        DFAGraph dfaGraph = DFAGraph.create(startDFAState);
        // 这个集合记录所有生成的 DFA 状态节点
        Set<DFAState> dfaStates = new HashSet<>();
        // 将开始状态节点添加到 dfaStates 中
        dfaStates.add(startDFAState);

        DFAState TState;
        // 从 dfaStates 集合中寻找一个未标记的 DFA 状态节点
        while ((TState = getNoTagState(dfaStates)) != null) {
            // 进行标记，防止重复遍历
            TState.setTag(true);

            // 遍历输入字符
            for (String path : DFAGraph.PATHS) {
                // 创建新的 DFA 状态节点
                DFAState UState = DFAState.create(closure(edge(TState, path)));
                // 不包含就添加
                if (!dfaStates.contains(UState)) {
                    dfaStates.add(UState);
                }
                // 添加转换表
                dfaGraph.addStateTable(TState, path, UState);
            }

        }
        return dfaGraph;
    }


    public static boolean isMatch(DFAGraph dfaGraph, String regex) {
        char[] chars = regex.toCharArray();
        int pos = 0;
        DFAState dfaState = dfaGraph.getStart();
        while (pos < chars.length) {
            String path = "" + chars[pos++];
            // 从转换表中获取当前状态 dfaState 遇到字符 path，得到下一个状态 dfaState
            dfaState = dfaGraph.getStateByMove(dfaState, path);
        }
        return dfaState.isEnd();
    }
    
    public static void main(String[] args) {
        String pattern = "a(b|c)*";

        NFAGraph nfaGraph = NFARegexUtil.createNFAGraph(pattern);
        nfaGraph.getEndState().setEnd(true);

        DFAGraph dfaGraph = NFAToDFA(nfaGraph);

        String regex = "abbccb";
        boolean isMatch = isMatch(dfaGraph, regex);
        System.out.println("isMatch(" + regex + "):" + isMatch);
    }
}
