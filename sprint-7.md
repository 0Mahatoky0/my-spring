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
- [ok] Creer methode dans execute(Method,HttpRequest) dans queryExecutor
  - [ok] recuprer les nom des arguments de la fonction
  - [ok] recuperer la valeur 
  - [ok] transformer le String au type de l arguments
  - [ok] executer la fonction avec les arguments passer