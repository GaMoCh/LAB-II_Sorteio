import java.util.function.BiFunction;

class Resident {
    private String id = "";
    private String name = "";
    private String phone = "";
    private Location location = new Location();
    private int dependentsAmount = -1;
    private double familyIncome = -1;

    Resident() {
    }

    Resident(String[] data) {
        setId(data[0]);
        setName(data[1]);
        setPhone(data[2]);
        setLocation(new Location(data[3], data[4], data[5], data[6], data[7], data[8], data[9]));
        setDependentsAmount(Integer.parseInt(data[10]));
        setFamilyIncome(Double.parseDouble(data[11]));
    }

    static boolean idIsValid(String id) {
        BiFunction<String, Integer, Boolean> isCheckDigit = (numbers, digitPosition) -> {
            int checkDigit = 0;

            for (int i = 0, j = digitPosition; i < digitPosition - 1; i++, j--)
                checkDigit += (numbers.charAt(i) - '0') * j;

            checkDigit = (checkDigit %= 11) < 2 ? 0 : 11 - checkDigit;

            return numbers.charAt(digitPosition - 1) - '0' == checkDigit;
        };

        id = id.replaceAll("\\D", "");

        if (id.length() != 11) return false;
        if (id.matches("\\D+")) return false;
        if (id.matches("^([0-9])\\1*$")) return false;
        if (!isCheckDigit.apply(id, 10)) return false;
        return isCheckDigit.apply(id, 11);
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        if (idIsValid(id))
            this.id = id;
        else
            throw new IllegalArgumentException();
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        if (!name.isEmpty())
            this.name = name;
        else
            throw new IllegalArgumentException();
    }

    String getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        if (phone.matches("^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$"))
            this.phone = phone;
        else
            throw new IllegalArgumentException();
    }

    Location getLocation() {
        return location;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    int getDependentsAmount() {
        return dependentsAmount;
    }

    void setDependentsAmount(int dependentsAmount) {
        if (dependentsAmount >= 0)
            this.dependentsAmount = dependentsAmount;
        else
            throw new IllegalArgumentException();
    }

    double getFamilyIncome() {
        return familyIncome;
    }

    void setFamilyIncome(double familyIncome) {
        if (familyIncome >= 0)
            this.familyIncome = familyIncome;
        else
            throw new IllegalArgumentException();
    }

    String toData() {
        StringBuilder data = new StringBuilder();

        data.append(id).append('\n')
                .append(name).append('\n')
                .append(phone).append('\n')
                .append(location.toData()).append('\n')
                .append(dependentsAmount).append('\n')
                .append(familyIncome).append('\n');

        return data.toString();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("CPF: ").append(id).append('\t')
                .append("Nome: ").append(name).append('\n')
                .append("Qtde. Dependentes: ").append(dependentsAmount).append('\t')
                .append("Renda Familiar ").append(String.format("R$%.2f", familyIncome)
                .replace(".", ",")).append('\n')
                .append("Telefone: ").append(phone).append('\n')
                .append(location).append('\n');

        return string.toString();
    }
}
