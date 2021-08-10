package xinhao.regex;

import java.util.*;

/**
 * @author by xinhao  2021/8/8
 */
public class NFARegexUtil {

    /**
     * 通过 pattern 生成对应的 NFAGraph 转换图
     * @param pattern
     * @return
     */
    public static NFAGraph createNFAGraph(String pattern) {
        Reader reader = Reader.create(pattern);
        NFAGraph graph = null;
        while (reader.hasNext()) {
            char ch = reader.next();
            switch (ch) {
                case '(' :
                    // 通过递归的方法，生成子串对应的 NFAGraph 转换图
                    NFAGraph subGraph = createNFAGraph(reader.readUntil(')'));
                    // 因为 * ? + 这些符号的优先级比 连接 高，就先处理
                    handleStar(subGraph, reader);
                    // 进行规制合并 NFAGraph 转换图
                    if (graph == null) {
                        graph = subGraph;
                    } else {
                        // 进行 连接 操作
                        graph.addSerial(subGraph);
                    }
                    break;
                case '|' :
                    // 通过递归的方法，生成子串对应的 NFAGraph 转换图
                    NFAGraph nextGraph = createNFAGraph(reader.readUntilEnd());
                    // 这里不需要处理  * ? + 这些符号，因为 reader.readUntilEnd() 已经读取了剩余的全部字符，不会有这些符号了。
                    if (graph == null) {
                        graph = nextGraph;
                    } else {
                        // 进行 并 操作
                        graph.addParallel(nextGraph);
                    }
                    break;
                default:
                    // 根据字符ch 生成一个小的 NFAGraph 转换图
                    NFAGraph pathGraph = NFAGraph.createByPath("" + ch);
                    // 因为 * ? + 这些符号的优先级比 连接 高，就先处理
                    handleStar(pathGraph, reader);
                    if (graph == null) {
                        graph = pathGraph;
                    } else {
                        // 进行 连接 操作
                        graph.addSerial(pathGraph);
                    }
                    break;
            }
        }

        return graph;
    }

    /**
     * 处理  * ? + 这些符号优先级
     * @param subGraph
     * @param reader
     * @return
     */
    private static NFAGraph handleStar(NFAGraph subGraph, Reader reader) {
        if (reader.hasNext()) {
            char ch = reader.peek();
            switch (ch) {
                case '*' :
                    // 0次或多次
                    subGraph.repeatStar();
                    reader.next();
                    break;
                case '?' :
                    // 0次 或 1 次
                    subGraph.zero();
                    reader.next();
                    break;
                case '+' :
                    // 1次以上
                    subGraph.repeatPlus();
                    reader.next();
                    break;
            }
        }
        return subGraph;
    }

    // 一个空集合Set 实例
    public static final Set<NFAState> EMPTY = new HashSet<>();

    /**
     * 对于一个输入字符串 regex , 在转换图 NFAGraph 中能否找到一个从初始状态到某个终止状态的转换序列。
     * 能找到，返回ture，表示能匹配
     * @param graph  转换图
     * @param regex  待匹配的字符串
     * @param recordState  记录一下匹配的路径
     * @return
     */
    public static boolean isMatch(NFAGraph graph, String regex, RecordNFAState recordState) {
        return isMatch(graph.getStartState(), regex.toCharArray(), 0, recordState);
    }

    /**
     * 通过递归调用来决定是否匹配
     * @param currentState
     * @param chars
     * @param pos
     * @param recordState
     * @return
     */
    public static boolean isMatch(NFAState currentState, char[] chars, int pos, RecordNFAState recordState) {

        // 当 pos == chars.length 时，表示已经匹配完最后一个字符。
        // 接下来就是看能否得到终止状态的节点
        if (pos == chars.length) {
            // 得到当前节点currentState 的 所有 ε 有向边。
            // 因为 FA 有最长子串匹配原则，所以优先先找空边(ε 有向边) 对应状态节点是不是终止状态的节点
            Set<NFAState> epsilonSet = currentState.getEdges().getOrDefault(NFAState.EPSILON, EMPTY);
            for (NFAState state : epsilonSet) {
                // 记录一下当前匹配路径
                recordState.setNextByPath(NFAState.EPSILON, state);
                // 递归调用 isMatch 方法，来判断是否匹配
                if (isMatch(state, chars, pos, recordState.getNext())) {
                    // 如果匹配，则直接返回 true
                    return true;
                }
            }

            // 当前节点是终止状态的节点，那么匹配成功，返回 true
            if (currentState.isEnd()) {
                return true;
            }

            // 如果以上都不匹配，而且已经匹配完最后一个字符，那么就说明这个 currentState 对应的匹配转换序列是不成功的。
            // 返回 false
            return false;
        }


        // 如果还没有匹配完所有的输入字符，那么就先匹配输入字符。

        // 优先匹配空边 (ε 有向边) 对应状态节点
        Set<NFAState> epsilonSet = currentState.getEdges().getOrDefault(NFAState.EPSILON, EMPTY);
        for (NFAState state : epsilonSet) {
            // 记录一下当前匹配路径
            recordState.setNextByPath(NFAState.EPSILON, state);
            // 递归调用 isMatch 方法，来判断是否匹配
            // 这里 pos 没有任何变化，因为是空边 (ε 有向边)，没有匹配任何字符
            if (isMatch(state, chars, pos, recordState.getNext())) {
                return true;
            }
        }

        String path = "" + chars[pos];
        // 当前节点的有向边集合中是否包含当前输入字符，
        if (currentState.getEdges().containsKey(path)) {
            // 得到当前输入字符对应的有向边集合
            Set<NFAState> pathSet = currentState.getEdges().getOrDefault(path, EMPTY);
            for (NFAState state : pathSet) {
                // 记录一下当前匹配路径
                recordState.setNextByPath(path, state);
                // 当前节点能不能找到一条匹配转换序列匹配剩下输入字符；
                // 这里将 pos + 1, 因为当前 pos 对应的输入字符已经匹配
                if (isMatch(state, chars, pos + 1, recordState.getNext())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 采用广度优先遍历的方式，来打印这个 NFAGraph 转换图中的有向边
     * @param nfaGraph
     */
    public static void printNFAGraphByBFS(NFAGraph nfaGraph) {
        // 采用广度优先遍历的算法，需要一个先入先出的队列数据结构来辅助
        Queue<NFAState> queue = new LinkedList<>();
        // 因为 NFAGraph 是一个图结构，而不是一个树结构，所以它的边不是单向的，存在循环指向的问题；
        // 因此我们需要一个 addedSet 集合来记录，已经添加到queue 队列中的节点，否则可能会进入死循环。
        // 注: 这里只要是添加到 queue 的节点，就马上添加到 addedSet 集合中。
        Set<NFAState> addedSet = new HashSet<>();
        queue.add(nfaGraph.getStartState());
        addedSet.add(nfaGraph.getStartState());

        StringBuilder builder = new StringBuilder();
        while (!queue.isEmpty()) {
            NFAState state = queue.poll();
            builder.append("状态"+state+":");
            for (Map.Entry<String, Set<NFAState>> entry : state.getEdges().entrySet()) {
                String path = entry.getKey();
                Set<NFAState> stateSet = entry.getValue();
                builder.append("\t路径["+path+"]有"+stateSet.size()+"条: ");
                for (NFAState childState : stateSet) {
                    // 如果没有添加过，那么就添加到 queue 队列中
                    if (!addedSet.contains(childState)) {
                        queue.add(childState);
                        addedSet.add(childState);
                    }
                    builder.append(state);
                    builder.append("--"+path+"-->");
                    builder.append(childState);
                    builder.append(";\t");
                }
                builder.append("\n\t\t\t");
            }
            builder.append("\n");
        }

        System.out.print(builder.toString());
    }

    public static void main(String[] args) {
        String pattern = "a(b|c)*";

        NFAGraph graph = createNFAGraph(pattern);
        // 设置转换图的结束状态节点 就是终止状态节点
        graph.getEndState().setEnd(true);

        String regex = "abbcbcb";
        RecordNFAState recordState = RecordNFAState.create(graph.getStartState());
        boolean isMatch = isMatch(graph, regex, recordState);
        System.out.println("isMatch(" + regex + "):" + isMatch);

        RecordNFAState rs = recordState;
        while (rs != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("[(状态" + rs.getState().getId() + ")");
            builder.append("--"+rs.getPath()+"-->");
            builder.append(rs.getNext() == null ? "null" : "(状态"+rs.getNext().getState().getId()+"）");
            builder.append("]");
            System.out.println(builder.toString());
            rs = rs.getNext();
        }
    }
    
}
