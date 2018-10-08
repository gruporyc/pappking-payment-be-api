package co.ppk.tests.repositories;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.wix.mysql.EmbeddedMysql;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;

public class DatasourceSingletonTest {

    private static DataSource instance = null;
    private static EmbeddedMysql mysqld;

    protected DatasourceSingletonTest() {
    }

    public static synchronized DataSource getInstance() throws IOException {
        if (instance == null) {
            MysqldConfig config = aMysqldConfig(v5_7_latest)
                    .withCharset(UTF8)
                    .withPort(2215)
                    .withUser("testuser", "testpassword")
                    .withTimeZone("Europe/Vilnius")
                    .withTimeout(2, TimeUnit.MINUTES)
                    .withServerVariable("max_connect_errors", 666)
                    .build();

            mysqld = anEmbeddedMysql(config)
                    .addSchema(SchemaConfig.aSchemaConfig("ppk_payments").build())
                    .start();

            instance = new HikariDataSource(new HikariConfig() {{
                setDriverClassName("com.mysql.jdbc.Driver");
                setJdbcUrl("jdbc:mysql://localhost:2215/ppk_payments");
                setUsername("testuser");
                setPassword("testpassword");
                addDataSourceProperty("cachePrepStmts", "true");
                addDataSourceProperty("prepStmtCacheSize", "250");
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                addDataSourceProperty("characterEncoding", "UTF-8");
                addDataSourceProperty("useUnicode", "true");
                addDataSourceProperty("serverTimezone", "UTC");
            }});
        }
        return instance;
    }

}
