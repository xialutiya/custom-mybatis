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
        /**
         * 获取执行器.
         */
        Executor executor = new SimpleExecutor();
        /**
         * 获取mappedStatement.
         */
        MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);
        /**
         * 执行新增操作.
         */
        return executor.update(configuration, mappedStatement, params);
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        /**
         * 获取执行器.
         */
        Executor executor = new SimpleExecutor();
        /**
         * 获取mappedStatement.
         */
        MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);
        /**
         * 执行新增操作.
         */
        return executor.update(configuration, mappedStatement, params);
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        /**
         * 获取执行器.
         */
        Executor executor = new SimpleExecutor();
        /**
         * 获取mappedStatement.
         */
        MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);
        /**
         * 执行新增操作.
         */
        return executor.update(configuration, mappedStatement, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        /**
         * 返回mapperClass的代理对象.
         */
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(),
                new Class[]{mapperClass}, (proxy, method, args) -> {
                    /**
                     * 方法名：例如deleteById
                     */
                    String methodName = method.getName();
                    /**
                     * deleteById方法所在class的全限定名.例如：xialu.mapper.StudyMapper
                     */
                    String className = method.getDeclaringClass().getName();

                    String statementId = className + "." + methodName;

                    /**
                     * 获取映射描述实体.
                     */
                    MappedStatement mappedStatement = configuration.getStatementMap().get(statementId);

                    StatementEnum type = mappedStatement.getType();

                    switch (type) {
                        case DELETE:
                            int delete = delete(statementId, args);
                            return delete;
                        case INSERT:
                            int insert = insert(statementId, args);
                            return insert;
                        case UPDATE:
                            int update = update(statementId, args);
                            return update;
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
