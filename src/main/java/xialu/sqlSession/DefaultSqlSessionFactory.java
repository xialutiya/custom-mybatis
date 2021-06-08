package xialu.sqlSession;


import xialu.entity.Configuration;

/**
 * 默认的sqlsession工厂类.
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    /**
     * 配置文件.
     */
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取默认的sqlsession.
     *
     * @return
     */
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
