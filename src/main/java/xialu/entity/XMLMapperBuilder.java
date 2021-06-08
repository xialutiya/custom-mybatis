package xialu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import xialu.enums.StatementEnum;
import xialu.utils.ParseNode;

import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;

@Builder
@AllArgsConstructor
@Data
public class XMLMapperBuilder implements ParseNode<Element, String> {

    private Configuration configuration;

    public void parse(InputStream is) throws DocumentException {

        /**
         * 使用dom4j解析mapper.xml文件流.
         */
        Document document = new SAXReader().read(is);
        /**
         * 获取根结点：<mapper></mapper>节点.
         */
        Element rootElement = document.getRootElement();
        /**
         * 获取名称空间(约定为dao层接口的全路径.).
         */
        String namespace = rootElement.attributeValue("namespace");
        /**
         * 获取所有的select的节点.
         */
        List<Element> selectList = rootElement.selectNodes("//select|//insert|//update|//delete");
        /**
         * 解析查询节点.
         */
        parseNode(this::parseMappedStatement, selectList, namespace);
    }

    /**
     * 解析映射描述节点.
     *
     * @param element
     * @param namespace
     */
    private void parseMappedStatement(Element element, String namespace) {
        /**
         * 利用dom4j解析文件流.
         */
        String id = element.attributeValue("id");
        String resultType = element.attributeValue("resultType");
        String paramterType = element.attributeValue("paramType");
        String sqlText = element.getTextTrim();
        StatementEnum anEnum = StatementEnum.getStatementByValue(element.getName());
        /**
         * 实例化映射描述实体.
         */
        MappedStatement mappedStatement = MappedStatement.builder().id(id)
                .resultType(resultType).paramterType(paramterType).sql(sqlText)
                .type(anEnum).build();
        /**
         * 约定为名称空间(dao层接口全路径:xialu.entity.Study)+id(接口名:findAll)
         */
        String key = namespace + "." + id;
        /**
         * 维护配置文件中的映射描述实体集合(一个mapper.xml中的语句对应一个MappedStatEment)
         */
        configuration.getStatementMap().put(key, mappedStatement);
    }

    /**
     * 解析节点.
     *
     * @param consumer
     * @param list
     */
    @Override
    public void parseNode(BiConsumer<Element, String> consumer, List<Element> list, String namespace) {
        list.forEach(n -> consumer.accept(n, namespace));
    }

}
