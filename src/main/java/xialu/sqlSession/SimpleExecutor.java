package xialu.sqlSession;


import xialu.config.BoundSql;
import xialu.entity.Configuration;
import xialu.entity.MappedStatement;
import xialu.utils.GenericTokenParser;
import xialu.utils.ParameterMapping;
import xialu.utils.ParameterMappingTokenHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

public class SimpleExecutor implements Executor {


    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        /**
         * 1. 注册驱动,获取数据库连接.
         */
        Connection connection = configuration.getDataSource().getConnection();

        /**
         * 2.获取sql语句：select id, name from study where name = #{name}
         */
        String sql = mappedStatement.getSql();
        /**
         * 3.解析sql语句,将占位符替换成问号,并获取占位符中的属性字段：select id, name from study where name = ?
         */
        BoundSql boundSql = getBoundSql(sql);

        /**
         * 5.获取预处理对象：preparedStatement
         */
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        /**
         * 6.获取参数全限定名.
         */
        String paramterType = mappedStatement.getParamterType();
        /**
         * 7.根据全限定名获取参数class类.
         */
        Class<?> paramtertypeClass = getClassType(paramterType);
        /**
         * sql语句中的字段集合.
         */
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        for (int i = 0; i < parameterMappingList.size(); i++) {
            /**
             *  8.获取参数字段.
             */
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            /**
             * 9.反射获取字段实例.
             */
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            /**
             * 私有属性允许访问.
             */
            declaredField.setAccessible(true);
            /**
             * 10.获取字段对应的值(入参).
             */
            Object o = declaredField.get(params[0]);
            /**
             * 11.添加参数.
             */
            preparedStatement.setObject(i + 1, o);

        }


        /**
         * 12.执行sql语句.
         */
        ResultSet resultSet = preparedStatement.executeQuery();
        /**
         * 13.获取返回结果集类型的全限定名:xialu.entity.Study.
         */
        String resultType = mappedStatement.getResultType();
        /**
         * 15.根据全限定名获取class类:Study.class.
         */
        Class<?> resultTypeClass = getClassType(resultType);

        ArrayList<Object> results = new ArrayList<>();

        /**
         * 16.封装结果集对象.
         */
        while (resultSet.next()) {
            /**
             * 17.实例化结果对象.
             */
            Object result = resultTypeClass.newInstance();
            /**
             * 18.获取结果集元数据.
             */
            ResultSetMetaData metaData = resultSet.getMetaData();
            /**
             * 19.遍历结果对象集合.
             */
            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                /**
                 * 20.获取字段名.
                 */
                String columnName = metaData.getColumnName(i);
                /**
                 * 21.获取字段值.
                 */
                Object columnValue = resultSet.getObject(columnName);

                /**
                 * 22.使用反射添加值到指定对象上.
                 */
                Map<String, Object> map = new HashMap<>(4);
                map.put(columnName, columnValue);
                setParamToObj(result, map);
            }
            /**
             * 23.添加映射完成的对象到结果集合.
             */
            results.add(result);

        }

        return (List<E>) results;
    }

    /**
     * 根据全限定名获取class类.
     *
     * @param path 全限定名：xlalu.entity.Study
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClassType(String path) throws ClassNotFoundException {

        Optional<String> optional = Optional.ofNullable(path);
        Class<?> aClass = null;

        if (optional.isPresent()) {

            aClass = Class.forName(optional.get());

            return aClass;
        }

        return aClass;
    }


    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {

        /**
         * 解析sql语句中的占位符.
         */
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        /**
         * 解析完成的sql.
         */
        String parseSql = genericTokenParser.parse(sql);
        /**
         * 解析出来的#{}中的属性集合.
         */
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);

        return boundSql;

    }

    /**
     * 根据反射添加结果到指定对象.
     *
     * @param obj 要添加的对象.
     * @param map key-属性名称,value-要添加的值.
     */
    private void setParamToObj(Object obj, Map<String, Object> map) {

        Class<?> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        map.keySet().forEach(maps -> Arrays.stream(declaredFields)
                .filter(field -> field.getName().equalsIgnoreCase(maps))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(obj, map.get(field.getName()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }


}
