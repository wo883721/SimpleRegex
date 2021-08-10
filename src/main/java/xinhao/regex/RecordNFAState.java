package xinhao.regex;


/**
 * @author by xinhao  2021/8/8
 * 记录一下匹配转换序列
 */
public class RecordNFAState {
    // 当前状态节点
    private NFAState state;
    // 匹配的下一个状态节点
    private RecordNFAState next;
    // 使用的匹配路径
    private String path;

    public RecordNFAState(NFAState state) {
        this.state = state;
    }

    public static RecordNFAState create(NFAState state) {
        return new RecordNFAState(state);
    }

    public void setNextByPath(String path, NFAState next) {
        this.path = path;
        this.next = create(next);
    }

    public NFAState getState() {
        return state;
    }

    public RecordNFAState getNext() {
        return next;
    }

    public String getPath() {
        return path;
    }
}
