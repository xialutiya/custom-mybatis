package xialu.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import xialu.entity.Configuration;
import xialu.entity.XMLMapperBuilder;
import xialu.io.Resources;
import xialu.utils.ParseNode;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Builder
@Data
@AllArgsConstructor
public class XMLConfigBuilder implements ParseNode<Element, Properties> {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = Configuration.builder().build();
    }

    public Configuration parseConfig(InputStream is) throws Exception {
        /**
         * 使用dom4j解析配置文件流.
         */
        Document document = new SAXReader().read(is);
        /**
         * 获取根结点:<configuration></configuration>节点.
         */
        Element rootElement = document.getRootElement();
        /**
         * 解析根结点下所有位置的property节点.
         */
        List<Element> list = rootElement.selectNodes("//property");
        /**
         * 用来存放后续节点中解析出来的键值对.
         */
        Properties properties = new Properties();
        /**
         * 解析数据库配置信息.
         */
        parseNode(this::parseDataSource, list, properties);
        /**
         * 添加数据库配置信息到配置信息实例中.
         */
        addDataSourceToConfig(properties);
        /**
         * 获取根结点下所有的mapper节点.
         */
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        /**
         * 解析mapper.xml文件.
         */
        parseNode(this::parseMappedStatement, mapperList);


        return configuration;
    }

    /**
     * 解析mapper.xml成MappedStatement写入到configuration.
     *
     * @param element
     */
    public void parseMappedStatement(final Element element) {
        String mapperPath = element.attributeValue("resource");
        InputStream resourceAsSteam = Resources.getResourceAsStream(mapperPath);
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
        try {
            xmlMapperBuilder.parse(resourceAsSteam);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析数据库配置信息.
     *
     * @param element
     */
    private void parseDataSource(final Element element, final Properties properties) {
        String name = element.attributeValue("name");
        String value = element.attributeValue("value");
        properties.setProperty(name, value);
    }

    private void addDataSourceToConfig(final Properties properties) throws PropertyVetoException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);
    }

    @Override
    public void parseNode(BiConsumer<Element, Properties> consumer, List<Element> list, Properties properties) {
        list.forEach(node -> consumer.accept(node, properties));
    }

    public void parseNode(Consumer<Element> consumer, List<Element> list) {
        list.forEach(node -> consumer.accept(node));
    }
}
