# objectif 
- Dans les controleurs on veux :
  - retourner une vue
  - envoyer des donnes vers les vues

# comment 
- [ok] cree une classe ModelAndView
  - [ok] atribut :
    - view
    - atribut <String,Object>
- dans les controleurs on return cette classe 
- dans frontServlet lors de l invocation de la fonction associer a l url   
  - on verifie si le valeur de retour est ModelAndView
  - si oui :
    - on migre les atributs du modelAndVue vers la response
    - on instancie un dispacher servlet avec comme url : prefix + modelAndView.url + sufix
      - tel que prefix et sufix , recupeer dans web.xml
    - on fait en suite dispacherServlet.forward(req,res)