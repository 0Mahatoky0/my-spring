# 📦 Jackson Java - Guide de Référence Rapide

Documentation synthétique des concepts de base et des configurations indispensables de Jackson (`com.fasterxml.jackson.databind.ObjectMapper`).

## 🎯 1. Les Bases Indispensables

### Règle d'or pour vos classes Java 🏗️

Pour que Jackson fonctionne correctement, la classe Java cible doit comporter :

1. Un **constructeur sans argument** (vide).

2. Des **Getters et Setters** pour tous les champs à mapper.

### Opérations de base : `ObjectMapper` 🛠️

```java
import com.fasterxml.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();

```

#### 📤 Sérialisation (Objet Java ➡️ JSON)

```java
Etudiant etudiant = new Etudiant("Alice", 22);

// Convertit l'objet en chaîne JSON
String json = mapper.writeValueAsString(etudiant);
// Résultat : {"nom":"Alice","age":22}

```

#### 📥 Désérialisation (JSON ➡️ Objet Java)

```java
String jsonInput = "{\"nom\":\"Bob\",\"age\":20}";

// Convertit la chaîne JSON en objet Java
Etudiant etudiant = mapper.readValue(jsonInput, Etudiant.class);

```

## 🏷️ 2. Annotations Utiles

Placées directement sur les attributs de vos classes Java pour ajuster le comportement du mapping.

| **Annotation 🏷️** | **Rôle ⚙️** | **Exemple d'utilisation** | 
| `@JsonProperty` | Renomme une clé JSON | `@JsonProperty("user_id") private int id;` | 
| `@JsonIgnore` | Ignore un champ (sérialisation ET désérialisation) | `@JsonIgnore private String motDePasse;` | 
| `@JsonInclude` | Exclut le champ s'il est `null` | `@JsonInclude(JsonInclude.Include.NON_NULL)` | 
| `@JsonFormat` | Définit le format d'une date | `@JsonFormat(pattern = "yyyy-MM-dd")` | 

### Exemple de classe annotée :

```java
import com.fasterxml.jackson.annotation.*;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL) // Appliqué à tous les champs de la classe
public class Utilisateur {

    @JsonProperty("user_id")
    private int id;

    private String nom;

    @JsonIgnore
    private String motDePasse;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    // Constructeurs, Getters et Setters obligatoires
}

```

## ⚙️ 3. Configurations Typiques

### A. Ignorer les propriétés inconnues dans le JSON 🛡️

Par défaut, Jackson lève une exception (`UnrecognizedPropertyException`) si le JSON contient un champ absent de la classe Java.

#### Option 1 : Via annotation sur la classe (Recommandé)

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonObjet {
    // ...
}

```

#### Option 2 : Configuration globale sur l'ObjectMapper

```java
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

```

### B. Formater le JSON produit (Pretty Print) 🎨

Pour rendre le JSON lisible avec des retours à la ligne et de l'indentation :

```java
// Option globale sur le mapper
mapper.enable(SerializationFeature.INDENT_OUTPUT);

// Ou à la volée lors de l'écriture
String jsonLisible = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(monObjet);

```

### C. Manipuler des Listes de Generics (`TypeReference`) 📚

Pour désérialiser un tableau JSON vers une `List<T>` Java :

```java
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

String jsonArray = "[{\"nom\":\"Alice\"}, {\"nom\":\"Bob\"}]";

// Utilisation de TypeReference pour conserver le type générique à l'exécution
List<Etudiant> liste = mapper.readValue(
    jsonArray, 
    new TypeReference<List<Etudiant>>() {}
);

```

### D. Support des types Date/Heure modernes (`java.time`) 📅

Pour gérer correctement des types comme `LocalDate` ou `LocalDateTime` :

1. Ajouter la dépendance Maven :

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>

```

2. Enregistrer le module sur l'`ObjectMapper` :

```java
mapper.registerModule(new JavaTimeModule());
// Pour éviter d'écrire les dates sous forme de timestamps numériques :
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

```