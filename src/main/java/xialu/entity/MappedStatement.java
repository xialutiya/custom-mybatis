package xialu.entity;

import lombok.*;
import xialu.enums.StatementEnum;

/**
 * 映射语句.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class MappedStatement {

    /**
     * 名称空间:namespace+id组成.
     */
    private String id;
    /**
     * 结果映射类型.
     */
    private String resultType;
    /**
     * 参数映射类型.
     */
    private String paramterType;
    /**
     * sql语句(未解析前到sql语句，包含占位符等).
     */
    private String sql;
    /**
     * sql语句的类型.
     */
    private StatementEnum type;
}
