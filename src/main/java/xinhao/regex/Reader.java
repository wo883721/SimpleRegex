package xinhao.regex;

/**
 * @author by xinhao  2021/8/8
 */
public class Reader {

    public static Reader create(String regex) {
        Reader reader = new Reader();
        reader.source = regex.toCharArray();
        return reader;
    }

    // 正则表达式对应的字符数组
    private char[] source;
    // 记录已经读取字符的下标
    private int pos;

    // 查看对应下标的字符， pos 不增加
    public char peek() {
        return source[pos];
    }

    // 获取pos下标对应的字符， 并将 pos 增加 1
    public char next() {
        if (pos >= source.length) {
            throw new RuntimeException("下标越界 length:" + source.length + " pos:" + pos);
        }
        return source[pos++];
    }

    // 是否还有下一个字符
    public boolean hasNext() {
        return pos < source.length;
    }

    // 一直读取到字符 ch
    public String readUntil(char ch) {
        StringBuilder builder = new StringBuilder();
        while (peek() != ch) {
            builder.append(next());
        }
        // 不包括 ch
        next();
        return builder.toString();
    }

    // 读取剩下的字符串
    public String readUntilEnd() {
        StringBuilder builder = new StringBuilder();
        while (hasNext()) {
            builder.append(next());
        }
        return builder.toString();
    }

}
