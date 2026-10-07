# Objectif
- Permetre le binding d objet dans les controllers

# todo
- Dans : MethodeExecutor
  - Si le type n est pas primitive ,c est objet
    - [ok] on instancie donc l objet
    - [ok] on recuperer la valeur de chaque atribut depuis req
    - on Recuperer les atributs de cette objets -> getObjectValue()
    - on verifie que l objet est 
    - getFieldParameterName(Field ,req) -> le nom du field etudiant.ecole.nom
    - getObjectValue(Type,req) -> return une instance de l objet avec les valeur des atribut recuperer dans req
      - pour chaque field -> la valeur des champs.
    - getStringValue(req,base) / base = le nom de l objet mere
    - getFieldValue(Field field,req)
    - Object parseString(Type,stringValue)

# etteur 
java.lang.IllegalArgumentException: wrong number of arguments: 2 expected: 1
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.checkArgumentCount(DirectMethodHandleAccessor.java:329)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:101)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)      tainerBase.java:776)
	at util.MethodExecutor.execute(MethodExecutor.java:26)             dardHost.java:772)
	at DispacherServlet.startProcessMapping(DispacherServlet.java:59)  .java:264)
	at DispacherServlet.doPost(DispacherServlet.java:47)               cute(InlineExecutorServic
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)  ache.catalina.core.Contai
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)  ache.catalina.core.Standa
	at 