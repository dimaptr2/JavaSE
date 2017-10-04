package ru.velkomfood.beagle.gpio;

import ch.eitchnet.beaglebone.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GPIOEngine implements Runnable {

    private static GPIOEngine instance;
    private final String GPIO_DIR = "/sys/class/gpio";
    private final Pin PIN_NUMBER = Pin.P8_45;
    private GpioBridge bridge;
    private Gpio gpio;
    private boolean flag;

    private DataSource dataSource;
    private Connection connection;

    private GPIOEngine() {
        flag = false;
        bridge = GpioBridgeImpl.getInstance();
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("srv-sapapp.eatmeat.ru");
        ds.setPort(3306);
        ds.setDatabaseName("beagle");
        ds.setUser("beagle");
        ds.setPassword("1qaz@WSX");
        dataSource = ds;
    }

    public static GPIOEngine getInstance() {
        if (instance == null) {
            instance = new GPIOEngine();
        }
        return instance;
    }

    public void initGPIO() throws GpioException {
        gpio = bridge.getGpio(Pin.P8_45, Direction.IN);
    }

    @Override
    public void run() {

        Signal origin = null;
        try {
            origin = bridge.readValue(gpio);
        } catch (GpioException e) {
            e.printStackTrace();
        }

        while (true) {

            try {
                connection = dataSource.getConnection();
                Signal currentSignal = bridge.readValue(gpio);
                if (!origin.equals(currentSignal)) {
                    origin = currentSignal;
                    flag = true;
                    sendMessage(connection);
                }
            } catch (GpioException | SQLException e) {
                System.out.println(e.getMessage());
                continue;
            } finally {
                try {
                    connection.close();
                } catch (SQLException sqe) {
                    System.out.println(sqe.getMessage());
                }
            }

        }
    }

    private void sendMessage(Connection conn) throws SQLException {

        if (flag) {
            String sql = "CALL zset_event()";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            flag = false;
        }

    }

}
