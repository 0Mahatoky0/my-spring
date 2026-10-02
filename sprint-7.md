# sprint 7
Bining
Dans les controller on ajoute la possibilite de mettre des parametres :
```java
// exemple
void saveUser(String nom , int age , double salaire) {
    //corps
}
```
Dans les affichages , on peut envoyer des dones (get / post)
On match avec les args
On put les donne dans les variables

## todo
- Creer methode dans execute(Method,HttpRequest) dans queryExecutor
  - recuprer les nom des arguments de la fonction
  - recuperer la valeur 
  - transformer le String au type de l arguments
  - executer la fonction avec les arguments passer