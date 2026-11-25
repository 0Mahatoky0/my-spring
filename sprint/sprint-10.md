# sprint 10 Url securiser
Connecter zay vo afaka miacceder methode
## comment
- [ok] Creer anotation @NeedLogin
- Creer fonction isConnected()
- Creer fonction showLoginWithMessageNeedConnexion()
- dans startProcessMapping :
  - verifier anotation @NeedLogin
  - si oui
    - si non connecter , showLoginWithMessageNeedConnexion()
- Dans DispacherServlet
- Dans processMapping
  - verifier si user est presend dans session
  - si il n est pas present : ne rien faire.
  - si il est persent sauvgarder.
