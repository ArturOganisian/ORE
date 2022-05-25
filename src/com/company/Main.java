package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;


public class Main {
    public static int[] Permute_array = new int[1000];
    public static int[] Reverse_Permute_array = new int[1000];
    public static int[] Varray = new int[1001];
    public static int count = 0;

    static SecretKey key;
    private static final int KEY_SIZE = 128;

    public static void init() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int[] Data_Base = new int[100];
        ORE_Setup();

        boolean isFinished = false;

        System.out.println("Warning!");
        System.out.println("Program works for numbers in a range [0,1,2..999]");

        while(!isFinished) {
            LogMenu();

            Scanner scn = new Scanner(System.in);
            int choose = scn.nextInt();

            switch(choose) {

                case 1:
                    System.out.println("How many elements to add?");
                    int arrSize = scn.nextInt();
                    addElement(arrSize, Data_Base);
                    break;
                case 2:
                    logDataBaseElements(Data_Base);
                    break;
                case 3:
                    System.out.println("Select the id of Data Base elements: ");
                    logDataBaseElements(Data_Base);

                    int id1 = scn.nextInt();
                    int id2 = scn.nextInt();

                    int text1 = Data_Base[id1 - 1];
                    int text2 = Data_Base[id2 - 1];

                    System.out.println("The comparison Result is: ");
                    ORE_Compare(text1 ,text2);
                    break;
                case 4:
                    for (int i = 0; i < Permute_array.length; i++) {
                        System.out.println("P[" + i + "] = " + Permute_array[i]);
                    }
                    break;
                case 5:
                    for (int i = 0; i < Reverse_Permute_array.length; i++) {
                        System.out.println("P^(-1)[" + i + "] = " + Reverse_Permute_array[i]);
                    }
                    break;
                case 6:
                    for (int i = 0; i < 1000; i++) {
                        System.out.println("V[" + i + "] = " + Varray[i]);
                    }
                    break;
                case 7:
                    isFinished = true;
                    break;
                default:
                    System.out.println("Error while input");
            }
        }
    }
    public static void logDataBaseElements(int[] data){
        System.out.println("Data Base elements are: ");
        for (int i = 0; i < data.length; i++) {
            if(data[i] != 0){
                System.out.print(data[i] + "  ");
            }
        }
        System.out.println();
    }

    public static void addElement(int size, int[] data) {
        for (int i = 0; i < size; i++) {
                System.out.println("Element " + (count + 1) + " is: ");
                Scanner scn = new Scanner(System.in);
                int inputText = scn.nextInt();
                data[count] = inputText;
                count++;
        }
    }

    public static void ORE_Setup() throws NoSuchAlgorithmException {
        init();
        Permutation();
        ReversePermutation(Permute_array);
    }

    public static String AES_Encrypt(int text, SecretKey key) {
        String s = String.valueOf(text);

        String encryptedData = null;
        try {
            AES_ENCRYPTION aes_encryption = new AES_ENCRYPTION(key);
            encryptedData = aes_encryption.encrypt(s);

            String decryptedData = aes_encryption.decrypt(encryptedData);

            return encryptedData;
        } catch (Exception ignored) {
        }
        return encryptedData;
    }

    public static String AES_Encrypt_Plus_R(int text, SecretKey key, byte[] r) {
        String s = String.valueOf(text);

        String encryptedData = null;
        try {
            AES_ENCRYPTION aes_encryption = new AES_ENCRYPTION(key);
            encryptedData = aes_encryption.encrypt(s,r);

            String decryptedData = aes_encryption.decrypt(encryptedData);

            return encryptedData;
        } catch (Exception ignored) {
        }
        return encryptedData;
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            String data = hexString.toString();
            return data;

        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static void ORE_Compare(int text1, int text2){
        String ctl = ORE_Left(text1);
        ORE_Right(text2);
        byte[] r = ByteBuffer.allocate(16).putInt(Varray[1000]).array();

        byte[] bytes = ctl.getBytes();
        int ctleft = ByteBuffer.wrap(bytes).getInt();


        String Hashing = sha256(AES_Encrypt_Plus_R(ctleft,key,r));
        String inputString = Hashing;
        byte[] byteArrray = inputString.getBytes();
        int num = ByteBuffer.wrap(byteArrray).getInt();

        int comparisonResult = Varray[Permute_array[text1]] - num%3;
        System.out.println(comparisonResult);
    }

    public static String ORE_Left(int text){
        int Permute_text = Permute_array[text];
        String s = AES_Encrypt(Permute_text, key);
        return s;
    }

    public static void ORE_Right(int text2){

        byte[] r = new byte[16];
        new SecureRandom().nextBytes(r);

        for (int i = 0; i < 1000; i++) {

            String Hashi = sha256(AES_Encrypt_Plus_R(i,key,r));

            String inputString = Hashi;
            byte[] byteArrray = inputString.getBytes();

            int num = ByteBuffer.wrap(byteArrray).getInt();

            Varray[i] = Compare(Reverse_Permute_array[i],text2) + (num)%3;
        }
        Varray[1000] = ByteBuffer.wrap(r).getInt();
    }

    public static int Compare(int txt1, int txt2){
    int comparison = 0;
        if(txt1 < txt2){
            comparison = -1;
        }
        if(txt1 == txt2){
            comparison = 0;
        }
        if(txt1 > txt2){
            comparison = 1;
        }
        return comparison;
    }


    public static void Permutation(){
        Random random = new Random();
        for (int i = 0; i < Permute_array.length; i++) {
            boolean found;
            int r;
            do {
                found = false;
                r = random.nextInt(1000 + 1);
                for (int j = 0; j < Permute_array.length; j++) {
                    if (Permute_array[j] == r || r == i+1) {
                        found = true;
                        break;
                    }
                }
            } while (found);
            Permute_array[i] = r;
        }
    }

    public static void ReversePermutation(int[] array){
        for (int i = 0; i < 1000; i++) {
            if(Permute_array[i] == 1000){
                continue;
            }
            Reverse_Permute_array[i] = Permute_array[Permute_array[i]];
        }
    }
    public static void LogMenu(){
        System.out.println("Type 1 for adding element to Data Base: ");
        System.out.println("Type 2 to show All added elements: ");
        System.out.println("Type 3 to select which elements need to be compared");
        System.out.println("Type 4 to see Permutation array");
        System.out.println("Type 5 to see Reverse Permutation array");
        System.out.println("Type 6 to see V[i] array:");
        System.out.println("Type 7 to exit application.");
    }
}
