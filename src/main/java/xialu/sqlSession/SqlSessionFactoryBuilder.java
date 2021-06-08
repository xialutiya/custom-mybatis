package xialu.sqlSession;

import lombok.*;
import xialu.config.XMLConfigBuilder;
import xialu.entity.Configuration;

import java.io.InputStream;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SqlSessionFactoryBuilder {

    private Configuration configuration;

    public SqlSessionFactory build(InputStream is) throws Exception {
        /**
         * 解析配置信息,封装到configuration中.
         */
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(is);

        /**
         * 实例化sqlSessionFactory对象.
         */
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        /**
         * 返回工厂对象.
         */
        return defaultSqlSessionFactory;
    }
}
