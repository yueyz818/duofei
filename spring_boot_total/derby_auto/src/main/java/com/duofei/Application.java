package com.duofei;

import com.duofei.derby.DerbyProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import java.sql.*;

/**
 * @author duofei
 * @date 2019/11/25
 */
@SpringBootApplication
public class Application implements ApplicationContextAware {

    @Bean
    public Connection derbyConnection(DerbyProperties derbyProperties) throws Exception{
        System.setProperty("jdbc.drivers", derbyProperties.getDrivers());
        return DriverManager.getConnection(derbyProperties.getUrl(),
                "duofei", "007");
    }

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        final Connection derbyConnection = applicationContext.getBean("derbyConnection", Connection.class);
        try {
            run(derbyConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void run(Connection conn) throws SQLException {
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate("CREATE TABLE Greetings (Message CHAR(40))");

            // Using ' not "
            stat.executeUpdate("INSERT INTO Greetings VALUES('hello')");
            stat.executeUpdate("INSERT INTO Greetings VALUES('你好，世界')");

            try(ResultSet result = stat.executeQuery("SELECT * FROM Greetings")){
                //将光标移动到下一行，初始在第一行之前
                while(result.next()){
                    System.out.println(result.getString("Message"));
                }
            }
            stat.executeUpdate("DROP TABLE Greetings");
        }finally {
            conn.close();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Application.applicationContext = applicationContext;
    }
}
