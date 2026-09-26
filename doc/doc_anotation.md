# Java — Créer ses propres annotations

---

## Structure de base

```java
// Déclaration minimale
public @interface MonAnnotation {
}

// Avec des éléments (attributs)
public @interface MonAnnotation {
    String  value();
    int     max()    default 10;
    boolean active() default true;
}
```

---

## Types autorisés pour les éléments

| Type | Exemple |
|------|---------|
| Primitifs | `int`, `boolean`, `double`… |
| `String` | `String value();` |
| `Class<?>` | `Class<?> handler();` |
| `enum` | `MonEnum niveau();` |
| Annotation | `AutreAnnotation meta();` |
| Tableau | `String[] tags();` |

```java
public @interface Exemple {
    String[]  tags();       // tableau
    Class<?>  handler();    // classe
    MonEnum   niveau();     // enum
    Autre     meta();       // annotation imbriquée
}
```

---

## Méta-annotations

### `@Target` — où peut-on l'utiliser ?

```java
@Target({
    ElementType.TYPE,              // classe, interface, enum
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.CONSTRUCTOR,
    ElementType.LOCAL_VARIABLE,
    ElementType.ANNOTATION_TYPE,
    ElementType.PACKAGE,
    ElementType.RECORD_COMPONENT   // Java 16+
})
```

### `@Retention` — jusqu'où survit-elle ?

```java
@Retention(RetentionPolicy.SOURCE)
// Supprimée après compilation. Ex: @Override, @SuppressWarnings

@Retention(RetentionPolicy.CLASS)
// Dans le .class, pas lisible en runtime. Défaut si @Retention absente.

@Retention(RetentionPolicy.RUNTIME)
// Lisible via Reflection. Indispensable pour traitement runtime.
```

### `@Documented`

```java
@Documented
public @interface MonAnnotation { }
// Inclut l'annotation dans la Javadoc générée.
```

### `@Inherited`

```java
@Inherited
public @interface MonAnnotation { }
// Les sous-classes héritent l'annotation posée sur la classe parent.
// Ne s'applique qu'aux classes, pas aux méthodes.
```

### `@Repeatable` (Java 8+)

```java
@Repeatable(Roles.class)
public @interface Role {
    String value();
}

public @interface Roles {
    Role[] value(); // conteneur obligatoire
}

// Utilisation :
@Role("admin")
@Role("user")
class Service { }
```

---

## Exemple complet — déclaration

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validate {
    int    min()     default 0;
    int    max()     default 100;
    String message() default "";
}
```

---

## Syntaxes d'utilisation

```java
// Élément unique nommé value() → raccourci sans nom
@Role("admin")
class A { }

// Plusieurs éléments
@Validate(min = 1, max = 50, message = "invalide")
void save() { }

// Tableau
@Permission(roles = {"admin", "mod"})
void delete() { }
```

---

## Lire une annotation via Reflection (RUNTIME)

### Sur une classe

```java
Class<?> clazz = MaClasse.class;

if (clazz.isAnnotationPresent(MonAnnotation.class)) {
    MonAnnotation ann = clazz.getAnnotation(MonAnnotation.class);
    System.out.println(ann.value());
}
```

### Sur les méthodes

```java
for (Method m : clazz.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Validate.class)) {
        Validate v = m.getAnnotation(Validate.class);
        System.out.println(m.getName() + " max=" + v.max());
    }
}
```

### Sur les champs

```java
for (Field f : clazz.getDeclaredFields()) {
    NotNull nn = f.getAnnotation(NotNull.class);
    if (nn != null) {
        // logique de validation...
    }
}
```

### Sur les paramètres

```java
Parameter[] params = method.getParameters();

for (Parameter p : params) {
    if (p.isAnnotationPresent(Inject.class)) {
        // injecter la valeur...
    }
}
```

---

## Cas d'usage typiques

### Validation de champ

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    String message() default "ne doit pas être vide";
}

// Usage :
@NotBlank
private String nom;
```

### Marqueur (sans attribut)

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly { }

// La présence suffit, pas besoin de valeur.
@ReadOnly
void getData() { }
```

### Mapping / config DSL

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String path();
    String method() default "GET";
}

@Route(path = "/users", method = "POST")
void createUser() { }
```

---

## Règles rapides

| Règle | Détail |
|-------|--------|
| `default` obligatoire ? | Non — sans `default`, l'utilisateur **doit** fournir la valeur. |
| `null` interdit | Un élément ne peut jamais retourner `null`. Utiliser `""` ou une valeur sentinelle. |
| `value()` seul | Si l'unique élément s'appelle `value()`, on peut écrire `@Ann("x")` au lieu de `@Ann(value = "x")`. |
| Sans `@Target` | L'annotation est applicable partout — à éviter, préférer être explicite. |
| Sans `@Retention` | Défaut = `CLASS`, donc **non lisible** en runtime via Reflection. |
| `extends` interdit | Une annotation ne peut pas étendre une autre annotation. |