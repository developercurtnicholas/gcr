package com.curt.TopNhotch.GCR.Utilities.Miscellaneous;

import java.util.Random;

public class RandomColorGenerator{

    public static String generateRandomColor(){

        Random rand = new Random();
        int num = rand.nextInt(5);
        switch (num){
            case 0: return "green";
            case 1: return "red";
            case 2: return "blue";
            case 3: return "gold";
            case 4: return "purple";
        }
        return "green";
    }
}
