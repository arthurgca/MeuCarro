package projetoi.meucarro.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luna on 11/07/2017.
 */

public class User {
    public String name;
    public String email;
    public String password;
    public String phone;
    public String ZIPcode;
    public int lastCarIndex;
    public List<Carro> cars;
    public List<Oficina> repairShops;

    public User() {

    }

    public User(String name, String email, String password, String phone, String ZIPcode, int lastCarIndex, List<Carro> cars, List<Oficina> repairShops) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.ZIPcode = ZIPcode;
        this.lastCarIndex = lastCarIndex;
        this.cars = cars;
        this.repairShops = repairShops;
    }

    public void addCar(Carro carro) {
        if (cars == null) {
            cars = new ArrayList<>();
        }
        cars.add(carro);
        lastCarIndex = cars.indexOf(carro);
    }

    public void changeCurrentCar(int index) {
        lastCarIndex = index;
    }

    public Carro currentCar() {
        return cars.get(lastCarIndex);
    }
}
