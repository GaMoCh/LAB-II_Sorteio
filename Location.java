class Location {
    private String address = "";
    private String number = "";
    private String complement = null;
    private String neighborhood = "";
    private String city = "";
    private String state = "";
    private String localCode = "";

    Location() {
    }

    Location(String address, String number, String complement, String neighborhood, String city, String state, String localCode) {
        this.address = address;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.localCode = localCode;
    }

    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        if (!address.isEmpty())
            this.address = address;
        else
            throw new IllegalArgumentException();
    }

    String getNumber() {
        return number;
    }

    void setNumber(String number) {
        if (number.matches("^\\d+$"))
            this.number = number;
        else
            throw new IllegalArgumentException();
    }

    String getComplement() {
        return complement;
    }

    void setComplement(String complement) {
        this.complement = complement;
    }

    String getNeighborhood() {
        return neighborhood;
    }

    void setNeighborhood(String neighborhood) {
        if (!neighborhood.isEmpty())
            this.neighborhood = neighborhood;
        else
            throw new IllegalArgumentException();
    }

    String getCity() {
        return city;
    }

    void setCity(String city) {
        if (!city.isEmpty())
            this.city = city;
        else
            throw new IllegalArgumentException();
    }

    String getState() {
        return state;
    }

    void setState(String state) {
        if (state.matches("^[a-zA-Z]{2}$"))
            this.state = state.toUpperCase();
        else
            throw new IllegalArgumentException();
    }

    String getLocalCode() {
        return localCode;
    }

    void setLocalCode(String localCode) {
        if (localCode.matches("^\\d{5}-\\d{3}$"))
            this.localCode = localCode;
        else
            throw new IllegalArgumentException();
    }

    String toData() {
        return address + '\n' +
                number + '\n' +
                complement + '\n' +
                neighborhood + '\n' +
                city + '\n' +
                state + '\n' +
                localCode;
    }

    @Override
    public String toString() {
        return "Logradouro: " + address + ", " + number +
                (complement.isEmpty() ? "" : ", " + complement) + '\n' +
                "Bairro: " + neighborhood + '\n' +
                "Cidade: " + city + '\t' +
                "Estado: " + state + '\t' +
                "CEP: " + localCode;
    }
}
