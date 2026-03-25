package com.zone.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class CodeGenerator {
    /**
     * 生成业务单号
     * @param prefix 前缀，如 "WO" 代表 Work Order
     * @return 唯一编号
     */
    public static String getBusCode(String prefix) {
        // 1. 获取当前时间戳（精确到秒，减少重复可能）
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        // 2. 加上 3 位随机数
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return prefix + timestamp + random;
    }
}