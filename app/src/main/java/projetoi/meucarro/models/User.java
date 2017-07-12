package projetoi.meucarro.models;

import java.util.List;

/**
 * Created by Luna on 11/07/2017.
 */

public class User {
    String name;
    String email;
    String password;
    String phone;
    String ZIPcode;
    Carro currentCar;
    List<Carro> cars;
    List<Oficina> repairShops;

    public User(String name, String email, String password, String phone, String ZIPcode, Carro currentCar, List<Carro> cars, List<Oficina> repairShops) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.ZIPcode = ZIPcode;
        this.currentCar = currentCar;
        this.cars = cars;
        this.repairShops = repairShops;
    }
}
