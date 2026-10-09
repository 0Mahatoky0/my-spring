# Objectif
- [ok] Permetre le binding d objet dans les controllers

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
