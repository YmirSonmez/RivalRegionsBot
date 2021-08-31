package me.ymir.rr;

import java.util.Scanner;

public class Core {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println(Color.GREEN_BOLD_BRIGHT + """
                ****************** RRCOM ******************
                Creator : YmirSG
                Telegram : @YmirSG
                Discord : YmirSG#5599
                RR Profile ID: 2000071179
                ****************** RRCOM ******************
                """);

        System.out.println(Color.RED_BRIGHT + """
                User information cannot be used anywhere outside of your computer.
                Not even the creator has access to your information.
                After logging into the account, the information is deleted. It is not stored anywhere.
                """);

        boolean breakWhile = false;
        Bot bot = null;

        while (true) {
            System.out.println(Color.GREEN + """
                    Please select login type...
                                
                    Facebook: F
                    Gmail: Not Supported
                    Vk: Not Supported
                    """);
            String typeV = "f";//scanner.nextLine();
            switch (typeV.toLowerCase()) {
                case "f" -> {
                    System.out.println(Color.BLUE + " Gmail/Username: ");
                    String gmail = scanner.nextLine();
                    System.out.println(Color.BLUE + " Password: ");
                    String password = scanner.nextLine();
                    bot = new Bot().facebookLogin(gmail, password);
                    breakWhile = true;
                }
                case "g" -> breakWhile = true;
                default -> System.out.println(Color.RED_BRIGHT + " Please select a valid type...");
            }
            if (breakWhile) break;
        }

        while (true) {
            if (bot == null) {
                System.out.println(Color.RED_BRIGHT + "Bot is null!");
                break;
            }
            System.out.println(Color.GREEN_BOLD_BRIGHT + """
                    ****************** RRCOM ******************
                    Select send operation...
                                    
                    National : N
                    Active : A
                    Voter : V
                    ****************** RRCOM ******************
                    """);
            String op = scanner.nextLine();
            System.out.println(Color.WHITE_BOLD_BRIGHT + "Write a message...");
            String text = scanner.nextLine();
            switch (op.toLowerCase()) {
                case "n" -> {
                    System.out.println(Color.WHITE_BOLD_BRIGHT + "Enter a nation ID...");
                    String ID = scanner.nextLine();
                    bot.sendNationMessage(text, ID);
                }
                case "v" -> {
                    System.out.println(Color.WHITE_BOLD_BRIGHT + "Enter a article ID...");
                    String ID1 = scanner.nextLine();
                    bot.sendVoters(text, ID1, false);
                }
                case "a" -> {
                    System.out.println(Color.WHITE_BOLD_BRIGHT + "Enter a goal...");
                    int goal = scanner.nextInt();
                    bot.sendMessageActivePlayers(goal, text);
                }
            }
            break;
        }
    }
}
