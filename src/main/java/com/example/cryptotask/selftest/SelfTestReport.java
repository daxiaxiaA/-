package com.example.cryptotask.selftest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class SelfTestReport {
    private final LocalDateTime startedAt = LocalDateTime.now();
    private final List<String> passedCases = new ArrayList<>();

    public void addPassedCase(String symmetricAlgorithm, String hashAlgorithm, int packageBytes) {
        passedCases.add(symmetricAlgorithm + " + " + hashAlgorithm + "，密文包 "
                + packageBytes + " 字节");
    }

    public int passedCount() {
        return passedCases.size();
    }

    public String toConsoleText() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELF_TEST_PASSED\n");
        builder.append("time: ")
                .append(startedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .append('\n');
        builder.append("cases: ").append(passedCases.size()).append('\n');
        for (String passedCase : passedCases) {
            builder.append("- ").append(passedCase).append('\n');
        }
        return builder.toString();
    }
}
