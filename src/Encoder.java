import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Encoder {

    private static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!]\r\n[»—…«-.\",:);?/(+%=*\\";
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        startup();
    }

    //Startup
    private static void startup() {
        boolean isCorrectInput = false;
        while (!isCorrectInput) {
            System.out.println("Пожалуйста, выберите свой вариант: [1. шифрование] [2. расшифровка] [3. Брут-форс] [4. Стат-анализ (НЕРАБОТАЕТ)] [5. Выход]");
            String scannerSelect = SCANNER.nextLine();

            if (scannerSelect.equalsIgnoreCase("1")) {
                encrypt();
                //encrypt(s, key);
                break;
            } else if (scannerSelect.equalsIgnoreCase("2")) {
                decrypt();
                //decrypt(s, key);
                break;
            } else if (scannerSelect.equalsIgnoreCase("3")) {
                brutForce();
                break;
            } else if (scannerSelect.equalsIgnoreCase("4")) {
                statAnalysis();
                break;
            } else if (scannerSelect.equalsIgnoreCase("5")) {
                System.out.println("Ок, досвидание.");
                isCorrectInput = true;
            } else {
                System.out.println("Вы ввели не правильный символ, будьте внимательны и повторите еще раз");
            }
        }
    }


    //Key Validation
    private static int keyValidation() {
        System.out.print("Пожалуйста, введите ключ, \n(Ключ должен состоять из целого числа от 0 до 500): ");
        int key = SCANNER.nextInt();
        return key;

        /*System.out.print("Ключ должен состоять из целого числа от 0 до 500 ");
        if (SCANNER.hasNextInt()) {
            key = SCANNER.nextInt();
            System.out.println(key);
        } else {
            System.out.println("Вы ввели не целое число, начните заново");
        }*/
    }


    //Get content from a file
    private static String getFileContent(String filePath) {
        Path path = Path.of(filePath);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Write to file
    private static void writeContentToFile(String content, String filePathPrevious, String suffix){
        int dotIndex = filePathPrevious.lastIndexOf(".");
        String fileBeforeDot = filePathPrevious.substring(0, dotIndex);
        String fileAfterDot = filePathPrevious.substring(dotIndex);
        String newFileName = fileBeforeDot + suffix + fileAfterDot ;
        //Files.write(Path.of(newFileName), content.getBytes(StandardCharsets.UTF_8));
        try {
            Files.writeString(Path.of(newFileName), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Encrypt
    private static void encrypt() {
        System.out.print("НАЧИНАЕМ ШИФРОВАТЬ \nВведите полный путь к файлу: ");
        String filePath = SCANNER.nextLine();
        String fileContent = getFileContent(filePath);
        System.out.print("Пожалуйста, введите ключ, \n(Ключ должен состоять из целого числа от 0 до 500): ");
        int key = SCANNER.nextInt();

        assert fileContent != null;
        writeContentToFile(encryptText(fileContent, key), filePath, "-encrypted");
    }

    private static String encryptText(String text, int key){
        StringBuilder stringBuilder = new StringBuilder();
        int newIndex = ALPHABET.length() + key;
        for (int i = 0; i < text.length(); i++) {
            int charIndex = ALPHABET.indexOf(text.charAt(i));
            char encrypted = ALPHABET.charAt((charIndex + newIndex) % ALPHABET.length());
            stringBuilder.append(encrypted);
        }
        return stringBuilder.toString();
    }


    //Decrypt
    private static void decrypt() {
        System.out.print("НАЧИНАЕМ РАСШИФРОВЫВАТЬ \nВведите полный путь к файлу: ");
        String filePath = SCANNER.nextLine();
        String fileContent = getFileContent(filePath);
        System.out.print("Пожалуйста, введите правильный ключ который Вы указывали при шифровании: ");
        int key = SCANNER.nextInt();

        assert fileContent != null;
        writeContentToFile(decryptText(fileContent, key), filePath, "-decrypted");
    }

    private static String decryptText(String encryptedText, int key) {
        StringBuilder stringBuilder = new StringBuilder();
        int newIndex = ALPHABET.length() - key;
        for (int i = 0; i < encryptedText.length(); i++) {
            int charIndex = ALPHABET.indexOf(encryptedText.charAt(i));
            char encrypted = ALPHABET.charAt((charIndex + newIndex) % ALPHABET.length());
            stringBuilder.append(encrypted);
        }
        return stringBuilder.toString();
    }


    private static void brutForce() {
        System.out.print("НАЧИНАЕМ ПОДБИРАТЬ КЛЮЧ МЕТОДОМ БРУТФОРС \nВведите полный путь к файлу: ");
        String filePath = SCANNER.nextLine();
        String fileContent = getFileContent(filePath);
        for (int i = 0; i < ALPHABET.length(); i++) {
            assert fileContent != null;
            String decryptedText = decryptText(fileContent, i);
            boolean isValid = isValidText(decryptedText);

            if (isValid) {
                System.out.println("Ключ равен " + i);
                writeContentToFile(decryptedText, filePath, "-brutted");
                break;
            }
        }
    }

    private static boolean isValidText(String text) {
        // Тест - на длину слов
        boolean inter = false;
        String[] strings = text.split(" ");
        for (String string : strings) {
            if (string.length() > 35) {
                return false;
            }
        }
        // Тест - на знаки
        if (text.contains(". ") || text.contains(", ") || text.contains("! ") || text.contains("? ")) {
            inter = true;
        }

        System.out.println("Понятен ли Вам это текст ?");
        int stringStart = new Random().nextInt(text.length() / 2);
        int stringEnd = stringStart + 150; // сделать проверку что бы не выйти за рамки текста - StringIndexOutOfBoundsException
        System.out.println(text.substring(stringStart, stringEnd));
        System.out.println("1. Все ОК");
        System.out.println("2. Нет текст не читаем");

        int i = SCANNER.nextInt();
        if (i == 1) {
            return true;
        } else if (i == 2) {
            return false;
        } else {
            System.out.println("Вы вели не верное значение"); //Сделать доп. проверку
        } // Сделать в цекле*/

        return true;
    }


    //Statistical analysis - здесь я ничего не понимаю ((
    private static void statAnalysis() {
        System.out.print("НАЧИНАЕМ РАСШИФРОВЫВАТЬ ЧЕРЕЗ СТАТИСТИЧЕСКИЙ АНАЛИЗ \nВведите полный путь к зашифрованному файлу: ");
        String filePath = SCANNER.nextLine();
        String decryptedText = getFileContent(filePath);

        System.out.print("Введите полный путь к файлу с текстом для получения статистики: ");
        String statFilePath = SCANNER.nextLine();
        String textForStatistics = getFileContent(statFilePath);
        HashMap<Character, Integer> decryptedTextStatistics = getCharacterStatistics(decryptedText);
        HashMap<Character, Integer> generalStatistics = getCharacterStatistics(textForStatistics);
        HashMap<Character, Character> characterStatistics = getCharacterStatistics(decryptedTextStatistics, generalStatistics);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < decryptedText.length(); i++) {
            char decrChar = characterStatistics.get(decryptedText.charAt(i));
            result.append(decrChar);
        }
        System.out.println(result.toString()); // Здесь нужно вернуть
    }


    private static HashMap<Character, Character> getCharacterStatistics(HashMap<Character, Integer> decryptedTextStatistics,  HashMap<Character, Integer> generalStatistics){
        HashMap<Character, Character> result = new HashMap<>();
        for (int i = 0; i < ALPHABET.length(); i++) {
            char c = ALPHABET.charAt(i);
            Integer characterStat = generalStatistics.get(c);
            Character closestCharacterFromStatMap = getClosestCharacterFromStatMap(decryptedTextStatistics, characterStat);
            result.put(closestCharacterFromStatMap, c);
        }
        return result;
    }

    private static Character getClosestCharacterFromStatMap(HashMap<Character, Integer> statMap, Integer value){
        Integer minDelta = Integer.MAX_VALUE;
        Character currentChar = 'c';
        for (Map.Entry<Character, Integer> characterIntegerEntry : statMap.entrySet()) {
            int delta = Math.abs(characterIntegerEntry.getValue() - value);
            if (delta < minDelta) {
                minDelta = delta;
                currentChar = characterIntegerEntry.getKey();
            }
        }
        return currentChar;
    }

    private static HashMap<Character, Integer> getCharacterStatistics(String text){
        HashMap<Character, Integer> resultAbsolute = new HashMap<>();
        HashMap<Character, Integer> result = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Integer integer = resultAbsolute.get(c);
            if (integer == 0) { // Не совсем понятно как работает.
                resultAbsolute.put(c, 1);
            } else {
                integer++;
                resultAbsolute.put(c, integer);
            }
        }
        for (Character character : resultAbsolute.keySet()) {
            Integer integer = resultAbsolute.get(character);
            int i = integer * 5_000 / text.length();
            result.put(character, i);
        }
        return result;
    }










}



