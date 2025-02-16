package top.ilovemyhome.peanotes.backend.duckdb;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    // 定义生成随机数据的范围
    private static final String[] FIRST_NAMES = {"John", "Jane", "Michael", "Emily", "Chris", "Jessica", "David", "Sarah", "Daniel", "Laura"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
    private static final String[] STREETS = {"Main St", "High St", "Maple Ave", "Oak Dr", "Pine Ln", "Cedar St", "Elm St", "Park Ave", "2nd St", "3rd St"};
    private static final String[] CITIES = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"};
    private static final String[] STATES = {"NY", "CA", "IL", "TX", "AZ", "PA", "TX", "CA", "TX", "CA"};
    private static final Random RANDOM = new Random();

    // 主方法
    public static void main(String[] args) {
        // 数据集大小
        int numberOfRecords = 1000000; // 可以根据需要调整
        // 输出文件路径
        String outputFilePath = "large_dataset.csv";

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            // 写入CSV表头
            writer.append("FirstName,LastName,Age,Email,Address,City,State,ZipCode\n");

            // 生成数据并写入文件
            for (int i = 0; i < numberOfRecords; i++) {
                // 生成随机数据
                String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
                int age = RANDOM.nextInt(50) + 18; // 年龄范围 18-67
                String email = generateEmail(firstName, lastName);
                String address = generateAddress();
                String city = CITIES[RANDOM.nextInt(CITIES.length)];
                String state = STATES[RANDOM.nextInt(STATES.length)];
                String zipCode = String.format("%05d", RANDOM.nextInt(99999)); // 生成五位数的邮政编码

                // 写入CSV文件
                writer.append(firstName)
                      .append(",")
                      .append(lastName)
                      .append(",")
                      .append(String.valueOf(age))
                      .append(",")
                      .append(email)
                      .append(",")
                      .append(address)
                      .append(",")
                      .append(city)
                      .append(",")
                      .append(state)
                      .append(",")
                      .append(zipCode)
                      .append("\n");
            }

            System.out.println("数据生成完成，输出文件：" + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 生成随机电子邮件
    private static String generateEmail(String firstName, String lastName) {
        String domain = "example.com";
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + domain;
    }

    // 生成随机地址
    private static String generateAddress() {
        int houseNumber = RANDOM.nextInt(9999) + 1; // 生成1到9999的门牌号
        String street = STREETS[RANDOM.nextInt(STREETS.length)];
        return houseNumber + " " + street;
    }
}
