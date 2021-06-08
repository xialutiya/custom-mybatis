package xialu.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有数据.
     */
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 查询一条数据.
     *
     * @param statementId 名称空间+id组成的唯一定位符.
     * @param params      参数.
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T selectOne(String statementId, Object... params) throws Exception;

    /**
     * 插入一条数据.
     *
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    int insert(String statementId, Object... params) throws Exception;

    /**
     * 修改一条数据.
     *
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    int update(String statementId, Object... params) throws Exception;

    /**
     * 删除一条数据.
     *
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    int delete(String statementId, Object... params) throws Exception;

    /**
     * 代理对象.
     *
     * @param mapperClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<?> mapperClass);


}
