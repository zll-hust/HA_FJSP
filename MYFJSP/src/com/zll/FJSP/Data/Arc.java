package com.zll.FJSP.Data;

/**
 * Description:邻域图的边
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月09日 下午07:12:24
 */
public class Arc {
    private Node next; // the node before or next this node
    private char restrainType; // Machine bound or Task bound
    // M: machine arc; J: dummy arc and precedence arc

    public Arc(Node next, char type) {
        this.next = next;
        this.restrainType = type;
    }

    public Node getNext() {
        return next;
    }

    public char getType() {
        return restrainType;
    }
}
