package xialu.sqlSession;

import lombok.AllArgsConstructor;
import xialu.entity.Configuration;
import xialu.entity.MappedStatement;
import xialu.enums.StatementEnum;

import java.lang.reflect.*;
import java.util.List;

/**
 * 默认的sqlsession.
 */
@AllArgsConstructor
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {

        /**
         * 获取执行器.
         */
        Executor executor = new SimpleExecutor();
        /**
         * 获取mappedStatement.
         */
        MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);
        /**
         * 执行sql语句.
         */
        List<Object> list = executor.query(configuration, mappedStatement, params);

        return (List<E>) list;
    }

    /**
     * 查询一条数据.
     *
     * @param statementId 名称空间+id组成的唯一定位符.
     * @param params      参数.
     * @param <T>
     * @return
     * @throws Exception
     */
    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        /**
         * 调用批量查询的接口.
         */
        List<Object> objects = selectList(statementId, params);
        /**
         * 批量查询返回结果为大小为一的集合,取第一条.
         */
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }


    }

    @Override
    public int insert(String statementId, Object... params) throws Exception {
        return 0;
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        return 0;
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return 0;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(),
                new Class[]{mapperClass}, (InvocationHandler) (proxy, method, args) -> {
                    // 底层都还是去执行JDBC代码 //根据不同情况，来调用selctList或者selectOne
                    // 准备参数 1：statmentid :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                    // 方法名：findAll
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();

                    String statementId = className + "." + methodName;

                    /**
                     * 获取映射描述实体.
                     */
                    MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);

                    StatementEnum type = mappedStatement.getType();

                    switch (type) {
                        case DELETE:
                            break;
                        case INSERT:
                            break;
                        case UPDATE:
                            break;
                        case SELECT:
                            /**
                             * 获取被调用方法的返回值类型
                             */
                            Type genericReturnType = method.getGenericReturnType();
                            /**
                             * 根据参数是否进行了泛型类型参数化，来判断执行的语句.
                             */
                            if (genericReturnType instanceof ParameterizedType) {
                                List<Object> objects = selectList(statementId, args);
                                return objects;
                            }
                            return selectOne(statementId, args);
                    }

                    return null;
                });

        return (T) proxyInstance;
    }


}
