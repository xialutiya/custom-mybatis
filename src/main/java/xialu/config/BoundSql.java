package xialu.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import xialu.utils.ParameterMapping;

import java.util.List;

/**
 * sql语句解析实体.
 */
@Data
@AllArgsConstructor
public class BoundSql {
    /**
     * 解析后的sql.
     */
    private String sqlText;
    /**
     * 从占位符解析出来的字段.
     */
    private List<ParameterMapping> parameterMappingList;
}
