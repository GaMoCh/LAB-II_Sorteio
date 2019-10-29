import utils.ExitCommandException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class App {
    private static String DATA_PATH = System.getProperty("user.dir") + "/data/data.txt";
    private static Scanner input = new Scanner(System.in);
    private static int menuItemSelectedId = 0;

    private static Map<String, Resident> residentsOfFirstBracket = new HashMap<>();
    private static Map<String, Resident> residentsOfSecondBracket = new HashMap<>();
    private static Map<String, Resident> residentsWaitingQueue = new LinkedHashMap<>();

    private static int maxAmountOfFirstBracketResidentsList = -1;
    private static int maxAmountOfSecondBracketResidentsList = -1;
    private static int maxAmountOfResidentsWaitingList = -1;
    private static double minimumWage = -1;

    private static final MenuItem[] menuItems = {
            new MenuItem("Cadastrar morador", () -> {
                Consumer<Resident> addInWaitingQueue = (resident) -> {
                    if (residentsWaitingQueue.size() < maxAmountOfResidentsWaitingList) {
                        residentsWaitingQueue.put(resident.getId(), resident);
                        printWarning("\nMorador adicionado na fila de espera");
                    } else
                        printWarning("\nTodas as listas já estão preenchidas");
                };

                if (checkIfParametersWereInformed()) {
                    Resident resident = new Resident();
                    String id;

                    if (residentsWaitingQueue.size() >= maxAmountOfResidentsWaitingList)
                        printWarning("A fila de espera está lotada");
                    else {
                        while (true) {
                            clearScreen();

                            try {
                                printLabel("CPF (Ex.: 999.999.999-99): ");
                                if (resident.getId().isEmpty()) {
                                    id = input.nextLine();
                                    if (!residentsOfFirstBracket.containsKey(id) &&
                                            !residentsOfSecondBracket.containsKey(id) &&
                                            !residentsWaitingQueue.containsKey(id))
                                        resident.setId(id);
                                    else {
                                        printWarning("\nCPF já cadastrado");
                                        waitConfirm();
                                        continue;
                                    }
                                } else
                                    System.out.println(resident.getId());

                                printLabel("Nome completo: ");
                                if (resident.getName().isEmpty())
                                    resident.setName(input.nextLine());
                                else
                                    System.out.println(resident.getName());

                                printLabel("Celular (Ex.: (99) 99999-9999): ");
                                if (resident.getPhone().isEmpty())
                                    resident.setPhone(input.nextLine());
                                else
                                    System.out.println(resident.getPhone());

                                printLabel("Logradouro: ");
                                if (resident.getLocation().getAddress().isEmpty())
                                    resident.getLocation().setAddress(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getAddress());

                                printLabel("Número: ");
                                if (resident.getLocation().getNumber().isEmpty())
                                    resident.getLocation().setNumber(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getNumber());

                                printLabel("Complemento: ");
                                if (resident.getLocation().getComplement() == null)
                                    resident.getLocation().setComplement(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getComplement());

                                printLabel("Bairro: ");
                                if (resident.getLocation().getNeighborhood().isEmpty())
                                    resident.getLocation().setNeighborhood(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getNeighborhood());

                                printLabel("Cidade: ");
                                if (resident.getLocation().getCity().isEmpty())
                                    resident.getLocation().setCity(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getCity());

                                printLabel("Estado (Ex.: XX): ");
                                if (resident.getLocation().getState().isEmpty())
                                    resident.getLocation().setState(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getState());

                                printLabel("CEP (Ex.: 99999-99): ");
                                if (resident.getLocation().getLocalCode().isEmpty())
                                    resident.getLocation().setLocalCode(input.nextLine());
                                else
                                    System.out.println(resident.getLocation().getLocalCode());

                                printLabel("Quantidade de dependentes: ");
                                if (resident.getDependentsAmount() < 0)
                                    resident.setDependentsAmount(Integer.parseInt(input.nextLine()));
                                else
                                    System.out.println(resident.getDependentsAmount());

                                printLabel("Renda familiar: ");
                                if (resident.getFamilyIncome() < 0)
                                    resident.setFamilyIncome(Double.parseDouble(input.nextLine()));
                                else
                                    System.out.println(resident.getFamilyIncome());

                            } catch (Exception e) {
                                printAlert();
                                waitConfirm();
                                continue;
                            }

                            break;
                        }

                        if (resident.getFamilyIncome() <= minimumWage) {
                            if (residentsOfFirstBracket.size() < maxAmountOfFirstBracketResidentsList) {
                                residentsOfFirstBracket.put(resident.getId(), resident);
                                printWarning("\nMorador adicionado na lista da primeira faixa");
                            } else
                                addInWaitingQueue.accept(resident);
                        } else if (resident.getFamilyIncome() <= minimumWage * 3) {
                            if (residentsOfSecondBracket.size() < maxAmountOfSecondBracketResidentsList) {
                                residentsOfSecondBracket.put(resident.getId(), resident);
                                printWarning("\nMorador adicionado na lista da segunda faixa");
                            } else
                                addInWaitingQueue.accept(resident);
                        } else
                            printWarning("\nO morador não está apto à participar do " +
                                    "sorteio devido o valor da sua renda familiar");
                    }

                    waitConfirm();
                }
            }),
            new MenuItem("Imprimir lista de moradores cadastrados", null),
            new MenuItem(1, "Listagem simples (apenas CPF e nome do morador)", () -> {
                if (checkIfParametersWereInformed()) {
                    printResidentsList("LISTAGEM DE MORADORES", 20, false, true, App::printPartialResident);
                }
            }),
            new MenuItem(1, "Listagem completa (todos os dados)", () -> {
                if (checkIfParametersWereInformed()) {
                    printResidentsList("LISTAGEM COMPLETA DE MORADORES", 3, false, false, System.out::println);
                }
            }),
            new MenuItem("Imprimir fila de espera", () -> {
                if (checkIfParametersWereInformed()) {
                    printResidentsList("LISTAGEM DE MORADORES NA FILA DE ESPERA", 20, true, true, App::printPartialResident);
                }
            }),
            new MenuItem("Pesquisar morador", () -> {
                if (checkIfParametersWereInformed()) {
                    String id;
                    Resident resident;

                    clearScreen();
                    printLabel("CPF (Ex.: 999.999.999-99): ");
                    id = input.nextLine();

                    if (!Resident.idIsValid(id))
                        printAlert();
                    else {
                        if ((resident = residentsOfFirstBracket.get(id)) != null) {
                            printLabel("\nFAIXA 1\n");
                            System.out.println(resident);
                        } else if ((resident = residentsOfSecondBracket.get(id)) != null) {
                            printLabel("\nFAIXA 2\n");
                            System.out.println(resident);
                        } else if ((residentsWaitingQueue.get(id)) != null) {
                            printWarning("\nMorador está na fila de espera");
                        } else {
                            printWarning("\nMorador nào foi encontrado");
                        }
                    }
                    waitConfirm();
                }
            }),
            new MenuItem("Desistência/Exclusão", () -> {
                if (checkIfParametersWereInformed()) {
                    String id;
                    Resident resident;
                    Map<String, Resident> list;

                    clearScreen();
                    printLabel("CPF (Ex.: 999.999.999-99): ");
                    id = input.nextLine();

                    if (!Resident.idIsValid(id))
                        printAlert();
                    else {
                        resident = residentsOfFirstBracket.get(id);
                        list = residentsOfFirstBracket;

                        if (resident == null) {
                            resident = residentsOfSecondBracket.get(id);
                            list = residentsOfSecondBracket;
                        }

                        if (resident != null) {
                            System.out.println("\n" + (list.get(id)));

                            System.out.print("Morador está na lista do sorteio, " +
                                    "para removê-lo digite a letra (S): ");

                            if (input.nextLine().equalsIgnoreCase("S")) {
                                if (!residentsWaitingQueue.isEmpty()) {
                                    list.remove(resident.getId());

                                    for (Resident residentInWaitingQueue : residentsWaitingQueue.values()) {
                                        if (list == residentsOfFirstBracket) {
                                            if (residentInWaitingQueue.getFamilyIncome() <= minimumWage) {
                                                residentsOfFirstBracket.put(residentInWaitingQueue.getId(), residentInWaitingQueue);
                                                residentsWaitingQueue.remove(residentInWaitingQueue.getId());
                                                break;
                                            }
                                        } else if (residentInWaitingQueue.getFamilyIncome() <= minimumWage * 3 &&
                                                residentInWaitingQueue.getFamilyIncome() > minimumWage) {
                                            residentsOfSecondBracket.put(residentInWaitingQueue.getId(), residentInWaitingQueue);
                                            residentsWaitingQueue.remove(residentInWaitingQueue.getId());
                                            break;
                                        }
                                    }
                                }

                                printWarning("\nMorador foi excluído");
                            } else
                                printWarning("\nMorador não foi excluído");
                            waitConfirm();
                            return;
                        }
                        resident = residentsWaitingQueue.get(id);

                        if (resident == null)
                            printWarning("\nMorador nào foi encontrado");
                    }
                    waitConfirm();
                }
            }),
            new MenuItem("Sorteio", () -> {
                if (checkIfParametersWereInformed()) {
                    Random random = new Random();
                    List keysAsArray;
                    int firstBracketHousesAmount = -1;
                    int secondBracketHousesAmount;
                    Map residentsOfFirstBracketCopy = new HashMap<>(residentsOfFirstBracket);
                    Map residentsOfSecondBracketCopy = new HashMap<>(residentsOfSecondBracket);

                    while (true) {
                        clearScreen();

                        try {
                            printLabel("Quantidade de casas sorteadas da primeira faixa: ");
                            if (firstBracketHousesAmount < 0) {
                                firstBracketHousesAmount = Integer.parseInt(input.nextLine());
                                if (firstBracketHousesAmount < 0)
                                    throw new IllegalArgumentException();
                            } else
                                System.out.println(firstBracketHousesAmount);

                            printLabel("Quantidade de casas sorteadas da segunda faixa: ");
                            secondBracketHousesAmount = Integer.parseInt(input.nextLine());
                            if (secondBracketHousesAmount < 0)
                                throw new IllegalArgumentException();
                        } catch (Exception e) {
                            printAlert();
                            waitConfirm();
                            continue;
                        }

                        if (firstBracketHousesAmount != 0 || secondBracketHousesAmount != 0) {
                            clearScreen();
                            printLabel("GANHADORES\n\n");

                            if (firstBracketHousesAmount != 0) {
                                printLabel("FAIXA 1\n");
                                for (int i = 0; i < firstBracketHousesAmount; i++) {
                                    if (!residentsOfFirstBracketCopy.isEmpty()) {
                                        keysAsArray = new ArrayList<>(residentsOfFirstBracketCopy.keySet());
                                        System.out.println(residentsOfFirstBracketCopy.remove(keysAsArray.get(random.nextInt(keysAsArray.size()))));
                                    }
                                }
                            }

                            if (secondBracketHousesAmount != 0) {
                                printLabel("FAIXA 2\n");
                                for (int i = 0; i < secondBracketHousesAmount; i++) {
                                    if (!residentsOfSecondBracketCopy.isEmpty()) {
                                        keysAsArray = new ArrayList<>(residentsOfSecondBracketCopy.keySet());
                                        System.out.println(residentsOfSecondBracketCopy.remove(keysAsArray.get(random.nextInt(keysAsArray.size()))));
                                    }
                                }
                            }

                            waitConfirm();
                        }
                        break;
                    }
                }
            }),
            new MenuItem("Parâmetros", () -> {
                while (true) {
                    clearScreen();

                    try {
                        printLabel("Quantidade máxima de moradores da primeira faixa: ");
                        if (maxAmountOfFirstBracketResidentsList < 0)
                            maxAmountOfFirstBracketResidentsList = Integer.parseInt(input.nextLine());
                        else
                            System.out.println(maxAmountOfFirstBracketResidentsList);

                        printLabel("Quantidade máxima de moradores da segunda faixa: ");
                        if (maxAmountOfSecondBracketResidentsList < 0)
                            maxAmountOfSecondBracketResidentsList = Integer.parseInt(input.nextLine());
                        else
                            System.out.println(maxAmountOfSecondBracketResidentsList);

                        printLabel("Quantidade máxima de moradores na fila de espera: ");
                        if (maxAmountOfResidentsWaitingList < 0)
                            maxAmountOfResidentsWaitingList = Integer.parseInt(input.nextLine());
                        else
                            System.out.println(maxAmountOfResidentsWaitingList);

                        printLabel("Valor do salário mínimo: ");
                        if (minimumWage < 0)
                            minimumWage = Double.parseDouble(input.nextLine());
                        else
                            System.out.println(minimumWage);
                    } catch (Exception e) {
                        printAlert();
                        waitConfirm();
                        continue;
                    }

                    waitConfirm();
                    break;
                }
            }),
            new MenuItem("Sair", () -> {
                throw new ExitCommandException();
            }),
    };

    private static void waitConfirm() {
        System.out.println("\033[1;34m\nPrecione ENTER para continuar...\033[0m");
        input.nextLine();
    }

    private static void printLabel(String message) {
        System.out.print("\033[1;36m" + message + "\033[0m");
    }

    private static void printWarning(String message) {
        System.out.println("\033[1;35m" + message + "\033[0m");
    }

    private static void printAlert() {
        System.out.println("\033[1;31m\nEntrada inválida\033[0m");
    }

    private static void clearScreen() {
        String newLines = "\n".repeat(32);
        System.out.print(newLines + "\033[H\033[2J");
        System.out.flush();
    }

    private static void showMenu() {
        StringBuilder menu = new StringBuilder();

        clearScreen();

        menu.append("\033[1;36m\t\tSORTEIO DE CASAS\n\n\033[0m");

        for (int i = 0; i < menuItems.length; i++)
            menu.append(" ".repeat(menuItems[i].getDepth() * 2))
                    .append(i == menuItemSelectedId ? "\033[1;34m\u2b9e\033[0m " : "  ")
                    .append(menuItems[i].getLabel())
                    .append('\n');

        System.out.println(menu);
    }

    private static boolean checkIfParametersWereInformed() {
        clearScreen();

        if (Arrays.stream(
                new Number[]{maxAmountOfFirstBracketResidentsList, maxAmountOfSecondBracketResidentsList, minimumWage}
        ).noneMatch((n) -> n.doubleValue() < 0))
            return true;
        else {
            printWarning("É necessário o preenchimento de todos os parâmetros");
            waitConfirm();
            return false;
        }
    }

    private static void printPartialResident(Resident resident) {
        String string = "CPF: " + resident.getId() + " - " +
                "Nome: " + resident.getName() + " - " +
                "Renda Familiar: " +
                String.format("R$%.2f", resident.getFamilyIncome())
                        .replace(".", ",");
        System.out.println(string);
    }

    private static void printResidentsList(String title, int residentsPerPage, boolean showWaitingQueue,
                                           boolean showLabel, Consumer<Resident> action) {
        Runnable printEmpty = () -> {
            clearScreen();
            printWarning("A Lista de moradores está vazia");
        };

        Consumer<Integer> printHeader = (page) -> {
            clearScreen();
            System.out.printf("\033[1;36m" + title + " (PÁGINA %d)\n", page);
            System.out.println("=".repeat(title.length() + 10 + String.valueOf(page).length()) + "\n\033[0m");
        };

        BiConsumer<AbstractMap.SimpleEntry<String, Map<String, Resident>>, Integer> printBody = (residents, start) -> {
            int position = 0;

            if (showLabel)
                System.out.println("\033[1;34m" + residents.getKey() + "\033[0m");

            for (Resident resident : residents.getValue().values()) {
                if ((position + start) != 0 && (position + start) % residentsPerPage == 0) {
                    waitConfirm();
                    printHeader.accept(((position + start) / residentsPerPage) + 1);
                }

                action.accept(resident);
                position++;
            }
        };

        if (showWaitingQueue) {
            if (residentsWaitingQueue.isEmpty())
                printEmpty.run();
            else {
                printHeader.accept(1);
                printBody.accept(new AbstractMap.SimpleEntry<>("FILA DE ESPERA", residentsWaitingQueue), 0);
            }
        } else {
            if (residentsOfFirstBracket.isEmpty() && residentsOfSecondBracket.isEmpty())
                printEmpty.run();
            else {
                printHeader.accept(1);
                printBody.accept(new AbstractMap.SimpleEntry<>("FAIXA 1", residentsOfFirstBracket), 0);
                printBody.accept(new AbstractMap.SimpleEntry<>("\nFAIXA 2", residentsOfSecondBracket), residentsOfFirstBracket.size());
            }
        }
        waitConfirm();
    }

    private static void writeData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_PATH))) {
            StringBuilder data = new StringBuilder();

            data.append(maxAmountOfFirstBracketResidentsList).append('\n');
            data.append(maxAmountOfSecondBracketResidentsList).append('\n');
            data.append(maxAmountOfResidentsWaitingList).append('\n');
            data.append(minimumWage).append('\n');

            data.append(residentsOfFirstBracket.size()).append('\n');
            for (Resident resident : residentsOfFirstBracket.values())
                data.append(resident.toData());

            data.append(residentsOfSecondBracket.size()).append('\n');
            for (Resident resident : residentsOfSecondBracket.values())
                data.append(resident.toData());

            data.append(residentsWaitingQueue.size()).append('\n');
            for (Resident resident : residentsWaitingQueue.values())
                data.append(resident.toData());

            writer.write(data.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void readData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH))) {
            BiConsumer<Integer, Map<String, Resident>> addResidents = (residentsAmount, list) -> {
                for (int i = 0; i < residentsAmount; i++) {
                    Resident resident;
                    String[] residentData = new String[12];
                    for (int j = 0; j < 12; j++) {
                        try {
                            residentData[j] = reader.readLine();
                        } catch (IOException ignored) {
                        }
                    }
                    resident = new Resident(residentData);
                    list.put(resident.getId(), resident);
                }
            };

            maxAmountOfFirstBracketResidentsList = Integer.parseInt(reader.readLine());
            maxAmountOfSecondBracketResidentsList = Integer.parseInt(reader.readLine());
            maxAmountOfResidentsWaitingList = Integer.parseInt(reader.readLine());
            minimumWage = Double.parseDouble(reader.readLine());

            addResidents.accept(Integer.parseInt(reader.readLine()), residentsOfFirstBracket);
            addResidents.accept(Integer.parseInt(reader.readLine()), residentsOfSecondBracket);
            addResidents.accept(Integer.parseInt(reader.readLine()), residentsWaitingQueue);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File(DATA_PATH);
        String line;

        file.getParentFile().mkdirs();
        file.createNewFile();

        if (file.length() != 0)
            readData();

        try {
            while (true) {
                showMenu();
                writeData();

                line = input.nextLine();

                if (line.isEmpty()) {
                    do {
                        menuItemSelectedId = menuItemSelectedId < menuItems.length - 1
                                ? menuItemSelectedId + 1 : 0;
                    } while (menuItems[menuItemSelectedId].getAction() == null);
                } else if (line.matches("^\\t+$"))
                    menuItems[menuItemSelectedId].run();
                else
                    break;
            }
        } catch (ExitCommandException ignored) {
        }
    }
}
