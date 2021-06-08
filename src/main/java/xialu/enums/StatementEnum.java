package xialu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum StatementEnum {


    SELECT("select", "查询"), INSERT("insert", "新增"),
    DELETE("delete", "删除"), UPDATE("update", "修改");

    private static Map<String, StatementEnum> map = new HashMap<>(8);

    static {
        Arrays.stream(StatementEnum.values()).forEach(v -> {
            map.put(v.value, v);
        });
    }

    /**
     * 值
     */
    private String value;
    /**
     * 描述.
     */
    private String desc;

    /**
     * 根据value获取对应枚举.
     *
     * @param value
     * @return
     */
    public static StatementEnum getStatementByValue(final String value) {
        return Optional.ofNullable(map.get(value)).orElseThrow(
                () -> new RuntimeException(String.format("不存在该类型sql:s%", value)));
    }
}
