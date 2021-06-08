package xialu.io;

import java.io.InputStream;

public class Resources {

    /**
     * 根据路径获取资源文件流.
     *
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(final String path) {
        InputStream is = Resources.class.getClassLoader().getResourceAsStream(path);
        return is;
    }
}
