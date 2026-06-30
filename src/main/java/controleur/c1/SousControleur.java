package controleur.c1;

import anotation.Controleur;
import anotation.GetMapping;
import anotation.UrlMapping;

@Controleur
public class SousControleur {
    @GetMapping("/get/getoto")
    public void sayGet() {
        System.out.println("Framework get");
    }

    @UrlMapping("/urlMap") 
    public void sayUrlMap() {
        System.out.println("urlMap");
    }
}
