package xialu.sqlSession;


import xialu.entity.Configuration;
import xialu.entity.MappedStatement;

import java.util.List;

/**
 * 执行器.
 */
public interface Executor {

    /**
     * 查询逻辑.
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

    /**
     * 更新逻辑.
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @return
     * @throws Exception
     */
    int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

}
