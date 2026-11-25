# Objectif
Singleton de controlleur 

# sous objectif
- Initialiser les controllers d est le demarage de l app
- initialiser les instances de controller dans le context
- mettre le contexte dans l atribut de methode executor 
- lors de l invocation :
  - recuperer l instance du controlleur dans le context

# todo
- dans l initalisation de l app :
  - recuperer la liste des controller
  - stoker la liste des controller dans le context
    - key   : ClasseController
    - value : instance du controller
  - ajouter atribut : context dans methodeExecutor
  - lores de l onvocation recuperer l instance , invoke sur l instance la fonction 