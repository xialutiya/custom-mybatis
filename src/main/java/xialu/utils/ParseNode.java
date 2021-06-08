package xialu.utils;

import org.dom4j.Element;

import java.util.List;
import java.util.function.BiConsumer;

public interface ParseNode<T, U> {
    void parseNode(BiConsumer<T, U> consumer, List<Element> list, U t);
}
