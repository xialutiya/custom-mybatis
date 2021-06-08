package xialu.entity;

import lombok.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件.
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    private DataSource dataSource;

    @Builder.Default
    Map<String, MappedStatement> statementMap = new HashMap<>(8);

}
