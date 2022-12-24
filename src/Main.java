
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        try {
            File cfg = new File("cfg.txt");
            Scanner epScanner = new Scanner(cfg);
            Scanner scanLines = new Scanner(cfg);

            //Counts lines
            int counter = 0;
            scanLines.nextLine();
            while(scanLines.hasNextLine()){
                scanLines.nextLine();
                counter++;
            }
            scanLines.close();
            //Variables that either contain € or has unit production to reach €.
            char[] eliminationCharacters = new char[counter];

            String elements = epScanner.nextLine();
            char[] elementArray = elements.toCharArray();

            //TODO might be used in further steps. If not, delete later.
            //Terminals (0-1 etc)
            char[] terminals = new char[elementArray.length];
            int j = 0;
            for (int i = 2; i < elementArray.length; i+=2){
                terminals[j] = elementArray[i];
                j++;
            }


            counter = 0;
            System.out.println("CFG form");
            //€ elimination start
            //Gets the lines that have €. (A-0B0|€)
            while (epScanner.hasNextLine()) {
                String data = epScanner.nextLine();
                char[] charArray = data.toCharArray();
                char currentSymbol = charArray[0];
                for (char c : charArray) {
                    if (c == '€') {
                        eliminationCharacters[counter] = currentSymbol;
                        counter++;
                    }
                }
                System.out.println(data);
            }


            epScanner.close();
            //€ finding.
            while(true){
                Scanner sc = new Scanner(cfg);
                sc.nextLine();
                boolean isFound = false;
                while(sc.hasNextLine()){
                    String data = sc.nextLine();
                    char[] charArray = data.toCharArray();
                    boolean isElimination = false;
                    boolean loopBreak = false;
                    //For all lines checks if our current line has €. (charArray[0] = beginning of line)
                    //If it has € than it is added to lines that are associated with having connects to € (lines that require operation.).
                    for (char eliminationCharacter : eliminationCharacters) {
                        if (eliminationCharacter == charArray[0]) {
                            isElimination = true;
                            break;
                        }
                    }
                    //Checks elimination characters to find if our line can produce € by going to lines that have €.
                    if(!isElimination){
                        for (int i = 0; i < eliminationCharacters.length; i++){
                            if(charArray[1] == '-' && charArray[3] == '|' && charArray[2] == eliminationCharacters[i]){
                                //Add line to lines that require operations.
                                eliminationCharacters[counter] = charArray[0];
                                counter++;
                                isFound = true;
                                break;
                            }
                            for(int b = 3; b < charArray.length - 1; b++){
                                if(charArray[b - 1] == '|' && charArray[b + 1] == '|' && charArray[b] == eliminationCharacters[i]){
                                    eliminationCharacters[counter] = charArray[0];
                                    counter++;
                                    loopBreak = true;
                                    isFound = true;
                                    break;
                                }
                            }
                            if(loopBreak) break;
                            if(charArray[charArray.length-2] == '|' && charArray[charArray.length - 1] == eliminationCharacters[i]){
                                eliminationCharacters[counter] = charArray[0];
                                counter++;
                                isFound = true;
                                break;
                            }

                        }
                    }
                }
                if(!isFound){
                    break;
                }
            }

            Scanner sc = new Scanner(cfg);
            sc.nextLine();
            //Type conversion for ease of operations.
            ArrayList<String> result = new ArrayList<String>();
            ArrayList<Character> eliminationArray = new ArrayList<Character>();
            for (char c : eliminationCharacters)
                eliminationArray.add(c);
            //Rows that must be operated on was obtained in the earlier step.
            //In this step operations are done to eliminate €.
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                char[] tempArray = data.toCharArray();

                //from 2 because this will be split, so we don't want the "S-" part.
                char[] tempArray2 = Arrays.copyOfRange(tempArray,2,tempArray.length);
                String tempStr = new String(tempArray2);

                String[] splitted;
                //Splits parts by "|".
                splitted = tempStr.split("\\|");

                String addToResult = new String(tempArray);
                StringBuilder tempAddToResult = new StringBuilder(addToResult);
                //Removes € from end result.
                int removeIndex = addToResult.indexOf("€");
                if(removeIndex != -1){
                    tempAddToResult.deleteCharAt(removeIndex);
                    if(removeIndex < tempAddToResult.length()){
                        if(tempAddToResult.charAt(removeIndex + 1) == '|') tempAddToResult.deleteCharAt(removeIndex + 1);
                    }
                    if(removeIndex > 0){
                        if(tempAddToResult.charAt(removeIndex - 1) == '|') tempAddToResult.deleteCharAt(removeIndex - 1);
                    }

                }

                addToResult = tempAddToResult.toString();
                result.add(addToResult);

                ArrayList<String> line = new ArrayList<>(Arrays.asList(splitted));
                boolean addBreak = false;
                for (int i = 0; i< line.size(); i++){

                    for(int a = 0; a < line.get(i).length(); a++){
                        //Gets the current part we are working with (A1A etc.).
                        StringBuilder addToResult2 = new StringBuilder(line.get(i));
                        //If this part contains a variable that can generate € removes that variable and gets the rest of the string.
                        //For example: A1A -> 1A
                        //Does the same for the second A:  A1A -> 1
                        //Goes into the nested for loop. Meanwhile A1A is saved in line ArrayList.
                        //This time skips the first A and gets the rest of the A's.
                        //Result will be: A1A -> A1 (last A removed.)
                        if(eliminationArray.contains(line.get(i).charAt(a))){
                            addToResult2.deleteCharAt(a);
                            addBreak = true;

                            addToResult2.insert(0,"|");

                            String resultString = addToResult2.toString();

                            result.add(resultString);

                            addToResult2.deleteCharAt(0);
                            int indexer = 1;
                            for(int z = a + 1; z < line.get(i).length(); z++){
                                if(eliminationArray.contains(line.get(i).charAt(z))){

                                    addToResult2.deleteCharAt(z - indexer);
                                    indexer++;
                                    addToResult2.insert(0,"|");

                                    resultString = addToResult2.toString();

                                    result.add(resultString);
                                    addToResult2.deleteCharAt(0);
                                }
                            }

                        }



                    }
                    if(addBreak){
                        result.add("\n");
                        addBreak = false;
                    }

                }
            }
            System.out.println("\nEliminate €");
           for (int i = 0; i < result.size(); i++){
               System.out.print(result.get(i) );
           }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}