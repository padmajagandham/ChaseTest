package com.test.apiservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class Helpers {

    public static int getRamdomInt(int withInRange){
        Random rand = new Random();
        return rand.nextInt(withInRange);
    }
}
