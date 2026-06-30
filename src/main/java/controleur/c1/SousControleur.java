package controleur.c1;

import anotation.Controleur;
import anotation.GetMapping;
import anotation.UrlMapping;

@Controleur
public class SousControleur {
    @GetMapping("/get")
    public void sayGet() {
        System.out.println("Framework get");
    }

    @UrlMapping(value = "/get",method = "GET") 
    public void sayUrlMap() {
        System.out.println("urlMap");
    }
}
