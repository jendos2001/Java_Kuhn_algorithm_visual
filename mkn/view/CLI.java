package mkn.view;

import mkn.controller.*;

import java.util.Locale;
import java.util.Scanner;

public class CLI implements View {
    private Controller controller;

    private StringBuilder log = new StringBuilder();
    private int stepCount = 1;

    private String help = new String("\"--help\" | \"-h\" - Print all commands with descriptions\n" +
            "\"--log\" | \"-l\" - Print all entered commands\n" +
            "\"--new-data\" | \"-nd\" - Reading data from a file\n" +
            "\"--to-start\" | \"-ts\" - Runs the algorithm to the start\n" +
            "\"--to-finish\" | \"-tf\" - Runs the algorithm to the finish\n" +
            "\"--next\" | \"-n\" - Starts the next step of the algorithm\n" +
            "\"--prev\" | \"-p\" - Starts the previous step of the algorithm\n" +
            "\"--exit\" | \"-e\" - Close program");

    private boolean toExit = false;
    private boolean isDataIn = false;
    private boolean isStart = false;
    private boolean isFinish = false;

    public CLI(){
        System.out.println("Demonstration of the Kuhn algorithm\n" +
                "To see all the commands, enter \"--help\" or \"-h\"");
    }

    @Override
    public void update() {
        String text = controller.getText();
        System.out.println(text);
        log.append(stepCount);
        log.append(") ");
        log.append(text);
        log.append("\n");
        stepCount++;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void exec() {
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.print("Input command: ");
            String command = input.nextLine();
            switch(command.toLowerCase(Locale.ROOT)) {
                case "--help":
                case "-h":
                    System.out.println(help);
                    break;

                case "--log":
                case "-l":
                    if(log.length() == 0)
                        System.out.println("Log is empty");
                    else
                        System.out.println(log);
                    break;

                case "--new-data":
                case "-nd":
                    System.out.println("Write path to \".txt\" file with data:");
                    String path = input.nextLine();
                    if(controller.getNewData(path)){
                        isStart = true;
                        isFinish = false;
                        isDataIn = true;
                        System.out.println("Write vertex to start:");
                        String vertex = input.nextLine();
                        controller.setStartVertex(vertex);
                    }
                    else
                        System.out.println("Incorrect file or data");
                    break;

                case "--to-start":
                case "-ts":
                    if(isDataIn && !isStart) {
                        controller.toStart();
                        isStart = true;
                        isFinish = false;
                    }
                    else
                        System.out.println("There is no data to analyze or algorithm on start");
                    break;

                case "--to-finish":
                case "-tf":
                    if(isDataIn && !isFinish) {
                        controller.toFinish();
                        isStart = false;
                        isFinish = true;
                    }
                    else
                        System.out.println("There is no data to analyze or algorithm on finish");
                    break;

                case "--next":
                case "-n":
                    if(isDataIn) {
                        if (isFinish) {
                            System.out.println("Algorithm is done!");
                            break;
                        }
                        if (controller.nextStep()) {
                            isFinish = true;
                            isStart = false;
                        } else {
                            isFinish = false;
                            isStart = false;
                        }
                    }
                    else
                        System.out.println("There is no data to analyze");
                    break;

                case "--prev":
                case "-p":
                    if(isDataIn) {
                        if (isStart) {
                            System.out.println("Algorithm is at the start");
                            break;
                        }
                        if (controller.prevStep()) {
                            isFinish = false;
                            isStart = true;
                        } else {
                            isFinish = false;
                            isStart = false;
                        }
                    }
                    else
                        System.out.println("There is no data to analyze");
                    break;

                case "--exit":
                case "-e":
                    System.out.println("Exiting");
                    toExit = true;
                    break;

                default:
                    System.out.println("Unknown command, try again");
            }
            if(toExit)
                break;
        }
    }
}