package kg.itschool.crm;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<String> cities = new ArrayList<>();

        cities.add("Bishkek");
        cities.add("Osh");
        cities.add("Naryn");
        cities.add("Talassss");
        cities.add("Djalal-Abad");
//        System.out.println(cities);

        cities.add(0, "Tokmok");
//        System.out.println(cities);

        cities.add(2, "Karakol");
//        System.out.println(cities);

        int talassssIndex = cities.indexOf("Talassss");
        cities.set(talassssIndex, "Talas");
        System.out.println(cities);

        List<String> cities2 = new ArrayList<>();
        cities2.add("Bishkek");
        cities2.add("Osh");
        cities2.add("Batken");
        cities2.add("Kant");
        cities2.add("Balykchy");
        System.out.println(cities2);

        List<String> union = new ArrayList<>(cities);
        union.addAll(cities2);
        System.out.println(union);

        List<String> intercept = new ArrayList<>(cities);
        intercept.retainAll(cities2);
        System.out.println(intercept);

        List<String> unique = new ArrayList<>(cities);
        cities2.removeAll(unique);
        System.out.println(cities2);
    }
}