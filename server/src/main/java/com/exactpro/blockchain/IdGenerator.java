package com.exactpro.blockchain;

import java.util.Random;

public class IdGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random RANDOM = new Random();

    public static String generateId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
