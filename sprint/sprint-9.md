# sprint 9 : Gestion des sessions
## objectif
- Permetre au developpeur de stoquer des variables dans le sesions
  
## Comment 
### Cote framwork
- Creer classe MySession
  - atribut :
    - Map<String,Object> value
- Dans DispacherServlet :
  - Verifier si il y a atribut MySession
  - si oui :
    - recuperer l atribut session dans httpSession
    - s il n existe pas ne rien faire 
    - si il existe l inecter dans le variable