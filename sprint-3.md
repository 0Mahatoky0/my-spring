# sprint 3
## Objectif 
Permete la diferenciation entre methode : 
- GET 
- POST
## Comment 
- Cree l anotation :
  - [ok] Url
    - [ok] atribut : 
      - value  : url
      - method : POST ou GET
    - [ok] method :
      - equals
- [en_cours] maper les methodes par Url (pas par string)
  - dans la methode getControleurMapping
    - recuperer l anotation Mapping
    - mettre comme cle cette urlMapping (pas le string)
  - recuperer la methode via la cle
  - verfifier si l encien code marche
# sprint 3-bis
- appeler la fonction lors de tapage de l url
    - cree une instance de Url (associer a l url et au methode)
    - recuperer la methode associer a l url
    - executer le mehode